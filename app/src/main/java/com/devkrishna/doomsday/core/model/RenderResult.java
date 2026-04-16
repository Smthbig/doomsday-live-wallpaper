package com.devkrishna.doomsday.core.model;

public class RenderResult {

    private final int total;
    private final int passed;

    public RenderResult(int total, int passed) {
        this.total = total;
        this.passed = passed;
    }

    public int getTotal() {
        return total;
    }

    public int getPassed() {
        return passed;
    }
}