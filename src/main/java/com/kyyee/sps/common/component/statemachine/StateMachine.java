package com.kyyee.sps.common.component.statemachine;

public interface StateMachine<T extends Enum<T>, K extends Enum<K>> {
    void addTransaction(T old, K evt, T next);

    T getNextState(T old, K evt);

    void addListener(StateMachineListener<T, K> l);

    void removeListener(StateMachineListener<T, K> l);

    void fireBeforeListener(T old, K evt, T next, Object... args);

    void fireAfterListener(T prev, K evt, T curr, Object... args);
}
