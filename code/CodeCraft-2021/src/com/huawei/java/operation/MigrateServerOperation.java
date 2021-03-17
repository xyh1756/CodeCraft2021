package com.huawei.java.operation;

public class MigrateServerOperation implements Operation{
    public final int vmID;
    public final int serverInstancePreID;
    public final int serverInstanceID;
    public final int Node;

    public MigrateServerOperation(int vmID, int serverInstancePreID, int serverInstanceID, int node) {
        this.vmID = vmID;
        this.serverInstancePreID = serverInstancePreID;
        this.serverInstanceID = serverInstanceID;
        Node = node;
    }
}
