package com.huawei.java.operation;

public class MigrateServerOperation implements Operation{
    public final int VMID;
    public final int ServerID;
    public final String Node;

    public MigrateServerOperation(int vmid, int serverID, String node) {
        VMID = vmid;
        ServerID = serverID;
        Node = node;
    }
}
