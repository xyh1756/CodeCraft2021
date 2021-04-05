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
    public PrintStream printStream;
    public final List<List<Operation>> buyServerOperations;
    public final List<List<Operation>> distributeServerOperations;
    public final List<List<Operation>> migrateServerOperations;
    public final String fileName;

    public OutputUtil(List<List<Operation>> buyServerOperations, List<List<Operation>> distributeServerOperations, List<List<Operation>> migrateServerOperations, String fileName) {
        this.buyServerOperations = buyServerOperations;
        this.distributeServerOperations = distributeServerOperations;
        this.migrateServerOperations = migrateServerOperations;
        this.fileName = fileName;
        try {
//            this.printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)), true);
//            System.setOut(this.printStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OutPut() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < buyServerOperations.size(); i++) {
            output.append("(purchase, ");
            output.append(buyServerOperations.get(i).size());
            output.append(")\n");
            for (Operation serverOperation : buyServerOperations.get(i)) {
                BuyServerOperation buyServerOperation = (BuyServerOperation) serverOperation;
                output.append("(");
                output.append(buyServerOperation.ServerType.getType());
                output.append(", ");
                output.append(buyServerOperation.number);
                output.append(")\n");
            }
            output.append("(migration, ");
            output.append(migrateServerOperations.get(i).size());
            output.append(")\n");
            for (Operation serverOperation : migrateServerOperations.get(i)) {
                MigrateServerOperation migrateServerOperation = (MigrateServerOperation) serverOperation;
                output.append("(");
                output.append(migrateServerOperation.vmID);
                output.append(", ");
                output.append(migrateServerOperation.serverInstanceID);
                if (migrateServerOperation.Node == 0)
                    output.append(", A)\n");
                else if (migrateServerOperation.Node == 1)
                    output.append(", B)\n");
                else
                    output.append(")\n");
            }
            for (Operation serverOperation : distributeServerOperations.get(i)) {
                DistributeServerOperation distributeServerOperation = (DistributeServerOperation) serverOperation;
                output.append("(");
                output.append(distributeServerOperation.serverInstance.getID());
                if (distributeServerOperation.Node == 0)
                    output.append(", A)\n");
                else if (distributeServerOperation.Node == 1)
                    output.append(", B)\n");
                else
                    output.append(")\n");
            }
        }
        System.out.print(output);
    }

    public void OutPutOneDay(List<Operation> buyServerOperationsDaily, List<Operation> distributeServerOperationsDaily, List<Operation> migrateServerOperationsDaily) {
        StringBuilder output = new StringBuilder();
        output.append("(purchase, ");
        output.append(buyServerOperationsDaily.size());
        output.append(")\n");
        for (Operation serverOperation : buyServerOperationsDaily) {
            BuyServerOperation buyServerOperation = (BuyServerOperation) serverOperation;
            output.append("(");
            output.append(buyServerOperation.ServerType.getType());
            output.append(", ");
            output.append(buyServerOperation.number);
            output.append(")\n");
        }
        output.append("(migration, ");
        output.append(migrateServerOperationsDaily.size());
        output.append(")\n");
        for (Operation serverOperation : migrateServerOperationsDaily) {
            MigrateServerOperation migrateServerOperation = (MigrateServerOperation) serverOperation;
            output.append("(");
            output.append(migrateServerOperation.vmID);
            output.append(", ");
            output.append(migrateServerOperation.serverInstanceID);
            if (migrateServerOperation.Node == 0)
                output.append(", A)\n");
            else if (migrateServerOperation.Node == 1)
                output.append(", B)\n");
            else
                output.append(")\n");
        }
        for (Operation serverOperation : distributeServerOperationsDaily) {
            DistributeServerOperation distributeServerOperation = (DistributeServerOperation) serverOperation;
            output.append("(");
            output.append(distributeServerOperation.serverInstance.getID());
            if (distributeServerOperation.Node == 0)
                output.append(", A)\n");
            else if (distributeServerOperation.Node == 1)
                output.append(", B)\n");
            else
                output.append(")\n");
        }
        System.out.print(output);
    }
}
