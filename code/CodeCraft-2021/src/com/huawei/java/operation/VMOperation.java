package com.huawei.java.operation;

public class VMOperation implements Operation{
    public final String type;
    public final String VMType;
    public final int ID;
    public final int number;

    public VMOperation(String type, String VMType, int id, int number) {
        this.type = type;
        this.VMType = VMType;
        this.ID = id;
        this.number = number;
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
