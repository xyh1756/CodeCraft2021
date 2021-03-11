package com.huawei.java.operation;

import com.huawei.java.model.Server;

public class BuyServerOperation implements Operation{
    public final Server ServerType;
    public final int number;

    public BuyServerOperation(Server serverType, int number) {
        ServerType = serverType;
        this.number = number;
    }
}
