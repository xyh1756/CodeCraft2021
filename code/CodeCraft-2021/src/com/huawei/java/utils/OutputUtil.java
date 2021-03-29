package com.huawei.java.utils;

import com.huawei.java.operation.BuyServerOperation;
import com.huawei.java.operation.DistributeServerOperation;
import com.huawei.java.operation.MigrateServerOperation;
import com.huawei.java.operation.Operation;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class OutputUtil {
    public final List<List<Operation>> buyServerOperations;
    public final List<List<Operation>> distributeServerOperations;
    public final List<List<Operation>> migrateServerOperations;
    public final String fileName;

    public OutputUtil(List<List<Operation>> buyServerOperations, List<List<Operation>> distributeServerOperations, List<List<Operation>> migrateServerOperations, String fileName) {
        this.buyServerOperations = buyServerOperations;
        this.distributeServerOperations = distributeServerOperations;
        this.migrateServerOperations = migrateServerOperations;
        this.fileName = fileName;
    }

    public void OutPut() throws FileNotFoundException {
//        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)), true);
//        System.setOut(ps);
        for (int i = 0; i < buyServerOperations.size(); i++) {
            System.out.printf("(purchase, %d)\n", buyServerOperations.get(i).size());
            for (Operation serverOperation : buyServerOperations.get(i)) {
                BuyServerOperation buyServerOperation = (BuyServerOperation) serverOperation;
                System.out.printf("(%s, %d)\n", buyServerOperation.ServerType.getType(), buyServerOperation.number);
            }
            System.out.printf("(migration, %d)\n", migrateServerOperations.get(i).size());
            for (Operation serverOperation : migrateServerOperations.get(i)) {
                MigrateServerOperation migrateServerOperation = (MigrateServerOperation) serverOperation;
                if (migrateServerOperation.Node == 0)
                    System.out.printf("(%d, %d, %s)\n", migrateServerOperation.vmID, migrateServerOperation.serverInstanceID, "A");
                else if (migrateServerOperation.Node == 1)
                    System.out.printf("(%d, %d, %s)\n", migrateServerOperation.vmID, migrateServerOperation.serverInstanceID, "B");
                else
                    System.out.printf("(%d, %d)\n", migrateServerOperation.vmID, migrateServerOperation.serverInstanceID);
            }
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
