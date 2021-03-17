package com.huawei.java.operation;

import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VMInstance;

public class MigrateServerOperation implements Operation{
    public final VMInstance vmInstance;
    public final ServerInstance serverInstancePre;
    public final ServerInstance serverInstance;
    public final int Node;

    public MigrateServerOperation(VMInstance vmInstance, ServerInstance serverInstancePre, ServerInstance serverInstance, int node) {
        this.vmInstance = vmInstance;
        this.serverInstancePre = serverInstancePre;
        this.serverInstance = serverInstance;
        Node = node;
    }
}
