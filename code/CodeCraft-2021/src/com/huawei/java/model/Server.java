package com.huawei.java.model;

import java.util.ArrayList;
import java.util.List;

public class Server implements Comparable<Server>{
    private final String type;
    private final int core;
    private final int memory;
    private final int hardwareCost;
    private final int energyCost;

    public Server(String type, int core, int memory, int hardwareCost, int energyCost) {
        this.type = type;
        this.core = core;
        this.memory = memory;
        this.hardwareCost = hardwareCost;
        this.energyCost = energyCost;
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

    public boolean isEnough(int core, int memory) {
        return this.core / 2 >= core && this.memory / 2 >= memory;
    }

    public boolean isEnoughDual(int core, int memory) {
        return this.core >= core && this.memory >= memory;
    }

    public int getHardwareCost() {
        return hardwareCost;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int compareTo(Server o) {
        return this.hardwareCost - o.hardwareCost;
    }
}
