package com.huawei.java.operation;

import com.huawei.java.model.VMInstance;

public class DeleteVMOperation implements Operation{
    public final int ID;

    public DeleteVMOperation(int ID) {
        this.ID = ID;
    }
}
