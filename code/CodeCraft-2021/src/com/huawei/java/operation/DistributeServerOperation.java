package com.huawei.java.operation;

import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VMInstance;

public class DistributeServerOperation implements Operation{
    public final int vmID;
    public final ServerInstance serverInstance;
    public final int Node;
    public final int number;

    public DistributeServerOperation(int vmID, ServerInstance serverInstance, int node, int number) {
        this.vmID = vmID;
        this.serverInstance = serverInstance;
        Node = node;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
