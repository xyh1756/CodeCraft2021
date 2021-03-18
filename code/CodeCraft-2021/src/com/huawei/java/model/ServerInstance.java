package com.huawei.java.model;

import com.huawei.java.main.Main;

import java.util.ArrayList;
import java.util.List;

public class ServerInstance implements Comparable<ServerInstance>{
    private final Server serverType;
    private int ID;
    private boolean running;
    private List<VMInstance> vmInstances = new ArrayList<>();
    private int ALeftCore;
    private int BLeftCore;
    private int ALeftMemory;
    private int BLeftMemory;

    public ServerInstance(Server serverType) {
        this.serverType = serverType;
        this.running = false;
        this.ALeftCore = serverType.getCore() / 2;
        this.ALeftMemory = serverType.getMemory() / 2;
        this.BLeftCore = this.ALeftCore;
        this.BLeftMemory = this.ALeftMemory;
    }

    public List<VMInstance> getVmInstances() {
        return vmInstances;
    }

    public void setVmInstances(List<VMInstance> vmInstances) {
        this.vmInstances = vmInstances;
    }

    public void addVmInstance(VMInstance vmInstance) {
        this.vmInstances.add(vmInstance);
        this.running = true;
    }

    public boolean delVmInstance(VMInstance vmInstance) {
        if (this.vmInstances.remove(vmInstance)) {
            if (vmInstance.getVmType().isDual()) {
                this.ALeftCore += vmInstance.getVmType().getCore() / 2;
                this.ALeftMemory += vmInstance.getVmType().getMemory() / 2;
                this.BLeftCore += vmInstance.getVmType().getCore() / 2;
                this.BLeftMemory += vmInstance.getVmType().getMemory() / 2;
            } else if (vmInstance.getNode() == 0) {
                this.ALeftCore += vmInstance.getVmType().getCore();
                this.ALeftMemory += vmInstance.getVmType().getMemory();
            } else {
                this.BLeftCore += vmInstance.getVmType().getCore();
                this.BLeftMemory += vmInstance.getVmType().getMemory();
            }
            return true;
        } else return false;
    }

    public Server getServerType() {
        return serverType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getALeftCore() {
        return ALeftCore;
    }

    public void setALeftCore(int ALeftCore) {
        this.ALeftCore = ALeftCore;
    }

    public int getBLeftCore() {
        return BLeftCore;
    }

    public void setBLeftCore(int BLeftCore) {
        this.BLeftCore = BLeftCore;
    }

    public int getALeftMemory() {
        return ALeftMemory;
    }

    public void setALeftMemory(int ALeftMemory) {
        this.ALeftMemory = ALeftMemory;
    }

    public int getBLeftMemory() {
        return BLeftMemory;
    }

    public void setBLeftMemory(int BLeftMemory) {
        this.BLeftMemory = BLeftMemory;
    }

    public boolean distributeA(int core, int memory) {
        if (ALeftCore >= core && ALeftMemory >= memory) {
            ALeftCore -= core;
            ALeftMemory -= memory;
            return true;
        } else
            return false;
    }

    public boolean distributeB(int core, int memory) {
        if (BLeftCore >= core && BLeftMemory >= memory) {
            BLeftCore -= core;
            BLeftMemory -= memory;
            return true;
        } else
            return false;
    }

    public boolean distributeDual(int core, int memory) {
        int dualCore = core / 2, dualMemory = memory / 2;
        if (ALeftCore >= dualCore && ALeftMemory >= dualMemory && BLeftCore >= dualCore && BLeftMemory >= dualMemory) {
            ALeftCore -= dualCore;
            ALeftMemory -= dualMemory;
            BLeftCore -= dualCore;
            BLeftMemory -= dualMemory;
            return true;
        } else
            return false;
    }

    @Override
    public int compareTo(ServerInstance o) {
        int resource1 = 0, resource2 = 0;
        for (VMInstance vmInstance : this.vmInstances) {
            resource1 += vmInstance.getVmType().getCore() + vmInstance.getVmType().getMemory();
        }
        for (VMInstance vmInstance : o.vmInstances) {
            resource2 += vmInstance.getVmType().getCore() + vmInstance.getVmType().getMemory();
        }
        return resource1 - resource2;
    }
}
