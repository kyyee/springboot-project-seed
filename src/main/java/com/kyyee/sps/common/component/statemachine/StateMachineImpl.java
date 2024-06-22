package com.kyyee.sps.common.component.statemachine;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.sps.common.exception.CloudStateMachineException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StateMachineImpl<T extends Enum<T>, K extends Enum<K>> implements StateMachine<T, K> {
    private final Map<T, HashMap<K, T>> _chart = new HashMap<>();
    private final List<StateMachineListener<T, K>> _listeners = new ArrayList<>();
    private final List<StateMachineListener<T, K>> _listenersTmp = new ArrayList<>();

    @Override
    public void addTransaction(T old, K evt, T next) {
        HashMap<K, T> entry = _chart.computeIfAbsent(old, k -> HashMap.newHashMap(1));
        entry.put(evt, next);
    }

    @Override
    public T getNextState(T old, K evt) {
        HashMap<K, T> entry = _chart.get(old);
        if (entry == null) {
            String err = "Cannot find next state:[old state: %s, state event: %s]".formatted(old, evt);
            throw CloudStateMachineException.of(BaseErrorCode.SYS_INTERNAL_ERROR, err);
        }

        T next = entry.get(evt);
        if (next == null) {
            String err = "Cannot find next state:[old state: %s, state event: %s]".formatted(old, evt);
            throw CloudStateMachineException.of(BaseErrorCode.SYS_INTERNAL_ERROR, err);
        }

        return next;
    }

    @Override
    public void addListener(StateMachineListener<T, K> l) {
        synchronized (_listeners) {
            _listeners.add(l);
        }
    }

    @Override
    public void removeListener(StateMachineListener<T, K> l) {
        synchronized (_listeners) {
            _listeners.remove(l);
        }
    }

    @Override
    public void fireBeforeListener(T old, K evt, T next, Object... args) {
        _listenersTmp.clear();
        synchronized (_listeners) {
            _listenersTmp.addAll(_listeners);
        }

        for (StateMachineListener<T, K> l : _listenersTmp) {
            try {
                l.before(old, evt, next, args);
            } catch (Exception e) {
                String err = "Unhandled exception while calling listener: %s before state changing.[current state:%s event: %s next state: %s]".formatted(l.getClass().getCanonicalName(), old, evt, next);
                log.warn(err, e);
            }
        }
    }

    @Override
    public void fireAfterListener(T prev, K evt, T curr, Object... args) {
        _listenersTmp.clear();
        synchronized (_listeners) {
            _listenersTmp.addAll(_listeners);
        }

        for (StateMachineListener<T, K> l : _listenersTmp) {
            try {
                l.after(prev, evt, curr, args);
            } catch (Exception e) {
                String err = "Unhandled exception while calling listener: %s after state changing.[previous state:%s event: %s current state: %s]".formatted(l.getClass().getCanonicalName(), prev, evt, curr);
                log.warn(err, e);
            }
        }
    }
}
