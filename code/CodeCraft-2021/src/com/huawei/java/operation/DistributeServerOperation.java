package com.huawei.java.operation;

import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VMInstance;

public class DistributeServerOperation implements Operation{
    public final VMInstance vmInstance;
    public final ServerInstance serverInstance;
    public final int Node;

    public DistributeServerOperation(VMInstance vmInstance, ServerInstance serverInstance, int node) {
        this.vmInstance = vmInstance;
        this.serverInstance = serverInstance;
        Node = node;
    }
}
