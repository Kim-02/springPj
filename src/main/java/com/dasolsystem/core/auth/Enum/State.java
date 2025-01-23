package com.dasolsystem.core.auth.Enum;

public enum State {
    OK(true),
    NULL(false),
    ;
    public final boolean value;
    State(boolean value) {
        this.value = value;
    }
}
