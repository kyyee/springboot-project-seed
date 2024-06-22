package com.kyyee.sps.common.component.workflow;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.common.component.statemachine.StateMachine;
import com.kyyee.sps.common.component.statemachine.StateMachineImpl;
import com.kyyee.sps.common.exception.ServiceException;
import com.kyyee.sps.common.exception.WorkFlowException;
import com.kyyee.sps.mapper.primary.WorkFlowChainMapper;
import com.kyyee.sps.mapper.primary.WorkFlowMapper;
import com.kyyee.sps.model.primary.WorkFlow;
import com.kyyee.sps.model.primary.WorkFlowChain;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AsyncWorkFlowChain {
    protected static final StateMachine<WorkFlowChainState, WorkFlowChainStateEvent> chainStates;
    protected static final StateMachine<WorkFlowState, WorkFlowStateEvent> flowStates;

    static {
        chainStates = new StateMachineImpl<>();
        chainStates.addTransaction(WorkFlowChainState.Processing, WorkFlowChainStateEvent.done, WorkFlowChainState.ProcessDone);
        chainStates.addTransaction(WorkFlowChainState.Processing, WorkFlowChainStateEvent.failed, WorkFlowChainState.ProcessFailed);
        chainStates.addTransaction(WorkFlowChainState.ProcessFailed, WorkFlowChainStateEvent.rollbackDone, WorkFlowChainState.RollbackDone);
        chainStates.addTransaction(WorkFlowChainState.ProcessDone, WorkFlowChainStateEvent.rollbackDone, WorkFlowChainState.RollbackDone);

        flowStates = new StateMachineImpl<>();
        flowStates.addTransaction(WorkFlowState.Processing, WorkFlowStateEvent.done, WorkFlowState.Done);
        flowStates.addTransaction(WorkFlowState.Processing, WorkFlowStateEvent.failed, WorkFlowState.Failed);
        flowStates.addTransaction(WorkFlowState.Failed, WorkFlowStateEvent.rollbackDone, WorkFlowState.RollbackDone);
        flowStates.addTransaction(WorkFlowState.Done, WorkFlowStateEvent.rollbackDone, WorkFlowState.RollbackDone);
    }

    protected enum ContinueStrategy {
        Restart,
        Nothing,
        Rollback,
    }

    protected WorkFlowChainMapper chainMapper;
    protected WorkFlowMapper flowMapper;
    @Getter
    protected String uuid;
    @Getter
    protected String name;
    @Getter
    protected String owner;
    protected List<AsyncWorkFlow> flows = new ArrayList<>();

    protected WorkFlowChain chain;
    protected WorkFlowCallback callback;
    protected int currentPosition = 0;
    private boolean isInitialized = false;
    @Getter
    @Setter
    private WorkFlowContext context;

    public AsyncWorkFlowChain(WorkFlowChainMapper chainMapper, WorkFlowMapper flowMapper) {
        this.chainMapper = chainMapper;
        this.flowMapper = flowMapper;
    }

    public AsyncWorkFlowChain(String name) {
        this.name = name;
    }

    public AsyncWorkFlowChain add(AsyncWorkFlow flow) {
        flows.add(flow);
        return this;
    }

    public AsyncWorkFlowChain compareAndAdd(AsyncWorkFlow flow) {
        if (!flows.contains(flow)) {
            flows.add(flow);
        }
        return this;
    }

    public AsyncWorkFlow get() {
        return get(currentPosition);
    }

    public AsyncWorkFlow get(int position) {
        if (position > flows.size()) {
            throw new IllegalArgumentException("position is wrong");
        } else {
            return flows.get(position);
        }
    }

    public AsyncWorkFlowChain build() {
        if (this.owner == null) {
            this.owner = "kyyee";
        }

        if (flows.isEmpty()) {
            throw new IllegalArgumentException("AsyncWorkFlowChain cannot be built without adding any WorkFlow in it");
        }

        StringBuilder sb = new StringBuilder(getName());
        sb.append(getOwner());
        for (AsyncWorkFlow f : flows) {
            String temp = f.getName();
            temp = temp == null ? f.getClass().getCanonicalName() : temp;
            sb.append(temp);
        }

        uuid = UUID.nameUUIDFromBytes(sb.toString().getBytes()).toString().replace("-", "");
        return this;
    }

    public AsyncWorkFlowChain setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public AsyncWorkFlowChain setName(String name) {
        this.name = name;
        return this;
    }

    protected void initialize() {
        if (getUuid() == null) {
            throw new IllegalArgumentException("WorkFlowChain cannot run before WorkFlowChain.build() is called");
        }

        chainMapper.deleteByPrimaryKey(getUuid());
        WorkFlowChain flowChain = new WorkFlowChain();
        flowChain.setName(getName());
        flowChain.setOwner(owner);
        flowChain.setUuid(getUuid());
        flowChain.setState(WorkFlowChainState.Processing);
        flowChain.setCurrentPosition(0);
        flowChain.setTotalWorkFlows(flows.size());
        flowChain.setOperationDate(LocalDateTime.now());
        chainMapper.insertSelective(flowChain);
        chain = flowChain;
    }

    protected void processFlow(AsyncWorkFlow flow, WorkFlowContext ctx, WorkFlow workFlow, int position) {
        if (workFlow == null) {
            workFlow = new WorkFlow();
            workFlow.setChainUuid(chain.getUuid());
            workFlow.setName(flow.getName());
            workFlow.setState(WorkFlowState.Processing);
            workFlow.setPosition(position);
            workFlow.setContext(ctx.toBytes());
            workFlow.setOperationDate(LocalDateTime.now());
            flowMapper.insertSelective(workFlow);
        } else {
            workFlow.setState(WorkFlowState.Processing);
            workFlow.setContext(ctx.toBytes());
            workFlow.setOperationDate(LocalDateTime.now());
            flowMapper.updateByPrimaryKeySelective(workFlow);
            chain.setState(WorkFlowChainState.Processing);
        }
        try {
            flow.process(this, ctx);
        } catch (WorkFlowException e) {
            try {
                fail(workFlow, e);
            } catch (Throwable t) {
                log.warn("Something seriously wrong happened when roll back", t);
            }
        } catch (Throwable t) {
            log.warn("workflow[{}] in chain[{}] failed because of an unhandle exception", flow.getName(), getName(), t);
            BaseErrorCode errorCode = BaseErrorCode.SYS_INTERNAL_ERROR;
            if (t.getCause() instanceof SocketTimeoutException) {
                errorCode = BaseErrorCode.REQUEST_TIMEOUT_ERROR;
            }
            try {
                fail(workFlow, ServiceException.of(errorCode.of()));
            } catch (Throwable t1) {
                log.warn("Something seriously wrong happened when roll back", t1);
            }
        }
    }

    private WorkFlow getFlowByPosition(int position) {
        Optional<WorkFlow> workFlow = flowMapper.wrapper()
            .eq(WorkFlow::getPosition, position)
            .eq(WorkFlow::getChainUuid, chain.getUuid()).one();
        return workFlow.orElseThrow();
    }

    private void tellCallbackSuccess(WorkFlowContext ctx) {
        try {
            callback.succeed(ctx);
        } catch (Throwable t) {
            log.warn("Unhandled exception in WorkFlowCallback[{}]", callback.getClass().getCanonicalName(), t);
        } finally {
            ctx.release();
        }
    }

    private void tellCallbackFailure(WorkFlowContext ctx, BaseException err) {
        try {
            callback.fail(ctx, err);
        } catch (Throwable t) {
            log.warn("Unhandled exception in WorkFlowCallback[{}]", callback.getClass().getCanonicalName(), t);
        } finally {
            ctx.release();
        }
    }

    public void runNext(WorkFlowContext ctx) {
        if (!isInitialized) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR.of(), "runNext() can only be called from AsyncWorkFlow");
        }

        WorkFlow current = getFlowByPosition(currentPosition);
        current.setState(flowStates.getNextState(current.getState(), WorkFlowStateEvent.done));
        current.setContext(ctx.toBytes());
        flowMapper.updateByPrimaryKeySelective(current);

        log.debug("Successfully processed workflow[{}] in chain[{}]", current.getName(), getName());
        currentPosition++;
        if (currentPosition < flows.size()) {
            chain.setCurrentPosition(currentPosition);
            chainMapper.updateByPrimaryKeySelective(chain);
            AsyncWorkFlow flow = flows.get(currentPosition);
            processFlow(flow, ctx, null, currentPosition);
        } else {
            chain.setState(WorkFlowChainState.ProcessDone);
            chainMapper.updateByPrimaryKeySelective(chain);
            tellCallbackSuccess(ctx);
        }
    }

    protected void rollbackFlow(WorkFlow workFlow) {
        AsyncWorkFlow flow = flows.get(workFlow.getPosition());
        WorkFlowContext ctx = WorkFlowContext.fromBytes(workFlow.getContext());
        try {
            flow.rollback(ctx);
            log.debug("Successfully rolled back AsyncWorkFlow[{}] in chain[{}]", flow.getName(), getName());
        } catch (Throwable t) {
            log.warn("Unhandled exception happened while rolling back AsyncWorkFlow[{}] in chain[{}]", flow.getName(), getName(), t);
        }
        workFlow.setState(flowStates.getNextState(workFlow.getState(), WorkFlowStateEvent.rollbackDone));
        flowMapper.updateByPrimaryKeySelective(workFlow);
    }

    public void rollback() {
        List<WorkFlow> workFlows = flowMapper.wrapper()
            .eq(WorkFlow::getChainUuid, chain.getUuid())
            .orderByDesc(WorkFlow::getPosition).list();

        log.debug("starting to rollback AsyncWorkFlowChain[name: {}, owner: {}]", name, owner);
        for (WorkFlow workFlow : workFlows) {
            if (workFlow.getState() == WorkFlowState.RollbackDone) {
                /* when this is called from carryOn(), some flows may have been rolled back, skip them */
                continue;
            }
            rollbackFlow(workFlow);
        }
        chain.setState(chainStates.getNextState(chain.getState(), WorkFlowChainStateEvent.rollbackDone));
        chainMapper.updateByPrimaryKeySelective(chain);
        log.debug("Rolled back all flows in AsyncWorkFlow chain[{}]", getName());
    }

    private void fail(WorkFlow workFlow, BaseException e) {
        workFlow.setReason(e.getMessage());
        workFlow.setErrorCode(e.getCode());
        workFlow.setState(flowStates.getNextState(workFlow.getState(), WorkFlowStateEvent.failed));
        log.debug("workflow[{}] in chain[{}] failed", workFlow.getName(), getName(), e);
        flowMapper.updateByPrimaryKey(workFlow);

        chain.setReason(e.getMessage());
        chain.setState(chainStates.getNextState(chain.getState(), WorkFlowChainStateEvent.failed));
        chain.setCurrentPosition(workFlow.getPosition());
        chainMapper.updateByPrimaryKeySelective(chain);
        rollback();
        WorkFlowContext ctx = WorkFlowContext.fromBytes(workFlow.getContext());
        tellCallbackFailure(ctx, e);
    }

    public void fail(AsyncWorkFlow flow, BaseException e) {
        int position = flows.indexOf(flow);
        WorkFlow vo = getFlowByPosition(position);
        fail(vo, e);
    }

    public void run(WorkFlowCallback callback) {
        run(null, callback);
    }

    public void run(WorkFlowContext ctx, WorkFlowCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }

        if (ctx == null) {
            ctx = new WorkFlowContext();
        }
        this.callback = callback;
        initialize();
        isInitialized = true;
        log.debug("starting to run AsyncWorkFlowChain[name: {}, owner: {}]", name, owner);
        AsyncWorkFlow flow = flows.get(currentPosition);
        processFlow(flow, ctx, null, currentPosition);
    }

    protected ContinueStrategy getContinueStrategy() {
        if (chain.getState() == WorkFlowChainState.ProcessDone || chain.getState() == WorkFlowChainState.RollbackDone) {
            return ContinueStrategy.Nothing;
        } else if (chain.getState() == WorkFlowChainState.ProcessFailed) {
            return ContinueStrategy.Rollback;
        } else if (chain.getState() == WorkFlowChainState.Processing) {
            List<WorkFlow> workFlows = flowMapper.wrapper()
                .eq(WorkFlow::getChainUuid, chain.getUuid())
                .orderByDesc(WorkFlow::getPosition).list();
            if (workFlows.isEmpty()) {
                return ContinueStrategy.Restart;
            }

            WorkFlow last = workFlows.getFirst();
            if (last.getState() == WorkFlowState.Processing) {
                return ContinueStrategy.Restart;
            } else if (last.getState() == WorkFlowState.Done) {
                return last.getPosition() == chain.getTotalWorkFlows() - 1 ? ContinueStrategy.Nothing : ContinueStrategy.Restart;
            } else if (last.getState() == WorkFlowState.RollbackDone) {
                WorkFlow first = workFlows.getLast();
                return first.getState() == WorkFlowState.RollbackDone ? ContinueStrategy.Nothing : ContinueStrategy.Rollback;
            } else if (last.getState() == WorkFlowState.Failed) {
                return ContinueStrategy.Rollback;
            }
        }

        throw new ServiceException("Program error: cannot find ContinueStrategy for work flow chain[uuid:%s]".formatted(chain.getUuid()));
    }

    public void carryOn(WorkFlowChain flowChain, WorkFlowCallback callback) throws WorkFlowException {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }

        this.chain = flowChain;
        this.callback = callback;
        this.name = this.chain.getName();
        this.owner = this.chain.getOwner();
        this.uuid = this.chain.getUuid();
        this.isInitialized = true;

        ContinueStrategy nextStep = getContinueStrategy();
        if (nextStep == ContinueStrategy.Nothing) {
            carryOnNothing();
        } else if (nextStep == ContinueStrategy.Restart) {
            carryOnRestart();
        } else if (nextStep == ContinueStrategy.Rollback) {
            carryOnRollback();
        }
        throw new ServiceException("Program error: cannot find ContinueStrategy for work flow chain[uuid:%s]".formatted(chain.getUuid()));
    }

    protected void carryOnRollback() {
        log.debug("Restart to roll back flows in work AsyncWorkFlowChain[uuid:{}]", chain.getUuid());
        Optional<WorkFlow> workFlow = flowMapper.wrapper()
            .eq(WorkFlow::getChainUuid, chain.getUuid())
            .isNotNull(WorkFlow::getReason).one();

        WorkFlow failedFlow = workFlow.orElseThrow(() -> new IllegalArgumentException("Cannot find workflow [chain_uuid:%s]".formatted(chain.getUuid())));

        rollback();
        WorkFlowContext ctx = WorkFlowContext.fromBytes(failedFlow.getContext());
        tellCallbackFailure(ctx, BaseException.of(BaseErrorCode.of(failedFlow.getErrorCode()), failedFlow.getReason()));
    }

    protected void carryOnRestart() throws WorkFlowException {
        List<WorkFlow> workFlows = flowMapper.wrapper()
            .eq(WorkFlow::getChainUuid, chain.getUuid())
            .orderByDesc(WorkFlow::getPosition).list();

        WorkFlow last = workFlows.getFirst();
        assert last.getState() == WorkFlowState.Done || last.getState() == WorkFlowState.Processing : "How can work flow[%s] in %s state when restart workflow chain[uuid:%s] !!?".formatted(last.getName(), last.getState(), chain.getUuid());
        int startPosition;
        WorkFlow start;
        if (last.getState() == WorkFlowState.Done) {
            startPosition = last.getPosition() + 1;
            start = null;
        } else {
            startPosition = last.getPosition();
            start = last;
        }
        log.debug("Restart flows in work flow chain[uuid:{}], start position is {}", chain.getUuid(), startPosition);
        WorkFlowContext ctx = WorkFlowContext.fromBytes(last.getContext());
        AsyncWorkFlow flow = flows.get(startPosition);
        this.currentPosition = startPosition;
        processFlow(flow, ctx, start, startPosition);
    }

    protected void carryOnNothing() {
        log.debug("Noting to carry on for work flow chain[uuid:{}]", chain.getUuid());
    }
}
