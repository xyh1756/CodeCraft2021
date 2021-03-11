package com.huawei.java.model;

public class VM {
    private final String type;
    private final int core;
    private final int memory;
    private final boolean isDual;

    public VM(String type, int core, int memory, boolean isDual) {
        this.type = type;
        this.core = core;
        this.memory = memory;
        this.isDual = isDual;
    }

    public String getType() {
        return type;
    }

    public int getCore() {
        return core;
    }

    public int getMemory() {
        return memory;
    }

    public boolean isDual() {
        return isDual;
    }
}
