package com.huawei.java.operation;

public class VMOperation implements Operation{
    public final String type;
    public final String VMType;
    public final int ID;

    public VMOperation(String type, String VMType, int id) {
        this.type = type;
        this.VMType = VMType;
        this.ID = id;
    }

    public String getType() {
        return type;
    }

    public String getVMType() {
        return VMType;
    }

    public int getID() {
        return ID;
    }
}
