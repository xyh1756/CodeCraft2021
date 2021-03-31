package com.huawei.java.operation;

import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VMInstance;

public class DistributeServerOperation implements Operation{
    public final int vmID;
    public final ServerInstance serverInstance;
    public final int Node;

    public DistributeServerOperation(int vmID, ServerInstance serverInstance, int node) {
        this.vmID = vmID;
        this.serverInstance = serverInstance;
        Node = node;
    }
}
