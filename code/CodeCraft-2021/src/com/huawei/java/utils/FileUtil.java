package com.huawei.java.utils;

import com.huawei.java.model.Server;
import com.huawei.java.model.VM;
import com.huawei.java.operation.Operation;
import com.huawei.java.operation.VMOperation;

import java.io.*;
import java.util.*;

public class FileUtil {
    public int serverNumber;
    public List<Server> serverList;
    public int vmNumber;
    public Map<String, VM> vms;
    public int days;
    public List<List<Operation>> operations;

    public FileUtil(String filePath) {
        serverList = new ArrayList<>();
        vms = new HashMap<>();
        operations = new ArrayList<>();
        try {
//            FileInputStream fileInputStream = new FileInputStream(filePath);
//            System.setIn(fileInputStream);
            Scanner scanner = new Scanner(System.in);
            String line;
            String[] information;
            serverNumber = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < serverNumber; i++) {
                line = scanner.nextLine();
                information = line.substring(1, line.length() - 1).split(",");
                serverList.add(new Server(information[0], Integer.parseInt(information[1].trim()),
                        Integer.parseInt(information[2].trim()), Integer.parseInt(information[3].trim()),
                        Integer.parseInt(information[4].trim())));
            }
            vmNumber = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < vmNumber; i++) {
                line = scanner.nextLine();
                information = line.substring(1, line.length() - 1).split(",");
                vms.put(information[0], new VM(information[0], Integer.parseInt(information[1].trim()),
                        Integer.parseInt(information[2].trim()), information[3].trim().equals("1")));
            }
            days = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < days; i++) {
                int orders = Integer.parseInt(scanner.nextLine());
                List<Operation> operations_daily = new ArrayList<>();
                for (int j = 0; j < orders; j++) {
                    line = scanner.nextLine();
                    information = line.substring(1, line.length() - 1).split(",");
                    if (information[0].equals("add"))
                        operations_daily.add(new VMOperation(information[0], information[1].trim(),
                                Integer.parseInt(information[2].trim()), j));
                    else
                        operations_daily.add(new VMOperation(information[0], null,
                                Integer.parseInt(information[1].trim()), j));
                }
                operations.add(operations_daily);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public int getVmNumber() {
        return vmNumber;
    }

    public Map<String, VM> getVms() {
        return vms;
    }

    public int getDays() {
        return days;
    }

    public List<List<Operation>> getOperations() {
        return operations;
    }
}
