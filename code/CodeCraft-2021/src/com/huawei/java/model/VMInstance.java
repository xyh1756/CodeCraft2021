package com.huawei.java.model;

public class VMInstance {
    private final VM vmType;
    private ServerInstance serverInstance;
    private final int ID;

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
}
