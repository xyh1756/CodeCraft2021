package com.huawei.java.operation;

import com.huawei.java.model.Server;
import com.huawei.java.model.ServerInstance;

public class BuyServerOperation implements Operation{
    public final Server ServerType;
    public int number;
    public final ServerInstance serverInstance;

    public BuyServerOperation(Server serverType, int number, ServerInstance serverInstance) {
        ServerType = serverType;
        this.number = number;
        this.serverInstance = serverInstance;
    }
}
