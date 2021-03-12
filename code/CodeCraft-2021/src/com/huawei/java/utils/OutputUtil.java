package com.huawei.java.utils;

import com.huawei.java.operation.BuyServerOperation;
import com.huawei.java.operation.DistributeServerOperation;
import com.huawei.java.operation.Operation;

import java.util.List;

public class OutputUtil {
    public final List<List<Operation>> buyServerOperations;
    public final List<List<Operation>> distributeServerOperations;

    public OutputUtil(List<List<Operation>> buyServerOperations, List<List<Operation>> distributeServerOperations) {
        this.buyServerOperations = buyServerOperations;
        this.distributeServerOperations = distributeServerOperations;
    }

    public void OutPut() {
        for (int i = 0; i < buyServerOperations.size(); i++) {
            System.out.printf("(purchase, %d)\n", buyServerOperations.get(i).size());
            for (Operation serverOperation : buyServerOperations.get(i)) {
                BuyServerOperation buyServerOperation = (BuyServerOperation) serverOperation;
                System.out.printf("(%s, %d)\n", buyServerOperation.ServerType.getType(), buyServerOperation.number);
            }
            System.out.println("(migration, 0)");
            for (Operation serverOperation : distributeServerOperations.get(i)) {
                DistributeServerOperation distributeServerOperation = (DistributeServerOperation) serverOperation;
                if (distributeServerOperation.Node == 0)
                    System.out.printf("(%d, %s)\n", distributeServerOperation.serverInstance.getID(), "A");
                else if (distributeServerOperation.Node == 1)
                    System.out.printf("(%d, %s)\n", distributeServerOperation.serverInstance.getID(), "B");
                else
                    System.out.printf("(%d)\n", distributeServerOperation.serverInstance.getID());
            }
        }
    }
}
