package com.huawei.java.model;

public class VMInstance {
    private final VM vmType;
    private ServerInstance serverInstance;
    private final int ID;
    private int Node;

    public VMInstance(VM vmType, int id) {
        this.vmType = vmType;
        ID = id;
    }

    public VM getVmType() {
        return vmType;
    }

    public ServerInstance getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }

    public int getID() {
        return ID;
    }

    public int getNode() {
        return Node;
    }

    public void setNode(int node) {
        Node = node;
    }

    public int getResource() {
        return vmType.getCore() + vmType.getMemory();
    }
}
