package com.huawei.java.operation;

import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VMInstance;

public class DistributeServerOperation implements Operation, Comparable<DistributeServerOperation>{
    public final VMInstance vmInstance;
    public final ServerInstance serverInstance;
    public final int Node;
    public final int Number;

    public DistributeServerOperation(VMInstance vmInstance, ServerInstance serverInstance, int node, int number) {
        this.vmInstance = vmInstance;
        this.serverInstance = serverInstance;
        Node = node;
        Number = number;
    }

    @Override
    public int compareTo(DistributeServerOperation o) {
        return this.Number - o.Number;
    }
}
