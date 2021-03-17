package com.huawei.java.utils;

import com.huawei.java.model.Server;
import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VM;
import com.huawei.java.model.VMInstance;
import com.huawei.java.operation.MigrateServerOperation;
import com.huawei.java.operation.Operation;
import com.huawei.java.operation.VMOperation;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JudgeUtil {
    public String outputFilePath;
    public Map<String, Server> serverMap;
    public Map<String, VM> vms;
    public List<List<Operation>> vmOperations;
    public List<List<Operation>> migrateServerOperations;

    public JudgeUtil(String outputFilePath, Map<String, Server> serverMap, Map<String, VM> vms, List<List<Operation>> vmOperations, List<List<Operation>> migrateServerOperations) {
        this.outputFilePath = outputFilePath;
        this.serverMap = serverMap;
        this.vms = vms;
        this.vmOperations = vmOperations;
        this.migrateServerOperations = migrateServerOperations;
    }

    public void Judge() {
        List<ServerInstance> serverInstanceList = new ArrayList<>();
        List<VMInstance> vmInstanceList = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(outputFilePath);
            System.setIn(fileInputStream);
            Scanner scanner = new Scanner(System.in);
            String line;
            String[] information;
            int serverInstanceCount = 0;
            for (int i = 0; i < 800; i++) {
                line = scanner.nextLine();
                information = line.substring(1, line.length() - 1).split(",");
                int serverTypeNumber = Integer.parseInt(information[1].trim());
                for (int j = 0; j < serverTypeNumber; j++) {
                    line = scanner.nextLine();
                    information = line.substring(1, line.length() - 1).split(",");
                    for (int k = 0; k < Integer.parseInt(information[1].trim()); k++) {
                        ServerInstance serverInstance = new ServerInstance(serverMap.get(information[0]));
                        serverInstance.setID(serverInstanceCount++);
                        serverInstanceList.add(serverInstance);
                    }
                }
                line = scanner.nextLine();
                information = line.substring(1, line.length() - 1).split(",");
                int migrateNumber = Integer.parseInt(information[1].trim());
                for (int j = 0; j < migrateNumber; j++) {
                    line = scanner.nextLine();
                    information = line.substring(1, line.length() - 1).split(",");
                    int vmID;
                    int serverInstanceID;
                    int Node;
                    if (information.length == 3) {
                        vmID = Integer.parseInt(information[0]);
                        serverInstanceID = Integer.parseInt(information[1].trim());
                        Node = information[2].trim().equals("A") ? 0 : 1;
                    } else {
                        vmID = Integer.parseInt(information[0]);
                        serverInstanceID = Integer.parseInt(information[1].trim());
                        Node = 2;
                    }
                    int serverInstancePreID = ((MigrateServerOperation) migrateServerOperations.get(i).get(j)).serverInstancePreID;
                    VMInstance vmInstance = null;
                    ServerInstance serverInstance = null;
                    ServerInstance serverInstancePre = null;
                    for (ServerInstance serverInstancethis : serverInstanceList) {
                        for (VMInstance vmInstanceThis : serverInstancethis.getVmInstances()) {
                            if (vmInstanceThis.getID() == vmID) {
                                vmInstance = vmInstanceThis;
                                break;
                            }
                        }
                        if (serverInstancethis.getID() == serverInstanceID)
                            serverInstance = serverInstancethis;
                        else if (serverInstancethis.getID() == serverInstancePreID)
                            serverInstancePre = serverInstancethis;
                    }
                    if (vmInstance == null || serverInstance == null || serverInstancePre == null)
                        System.out.println("error while searching vms or servers by id");
                    assert serverInstance != null;
                    assert vmInstance != null;
                    assert serverInstancePre != null;
                    if (Node == 2) {
                        if (serverInstance.distributeDual(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                            serverInstance.addVmInstance(vmInstance);
                            vmInstance.setServerInstance(serverInstance);
                            serverInstancePre.delVmInstance(vmInstance);
                        } else {
                            System.out.println("error while migrating vm");
                        }
                    } else if (Node == 0) {
                        if (serverInstance.distributeA(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                            serverInstance.addVmInstance(vmInstance);
                            vmInstance.setServerInstance(serverInstance);
                            vmInstance.setNode(0);
                            serverInstancePre.delVmInstance(vmInstance);
                        } else {
                            System.out.println("error while migrating vm");
                        }
                    } else {
                        if (serverInstance.distributeB(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                            serverInstance.addVmInstance(vmInstance);
                            vmInstance.setServerInstance(serverInstance);
                            vmInstance.setNode(1);
                            serverInstancePre.delVmInstance(vmInstance);
                        } else {
                            System.out.println("error while migrating vm");
                        }
                    }
                }
                for (int j = 0; j < vmOperations.get(i).size(); j++) {
                    if (((VMOperation) vmOperations.get(i).get(j)).getType().equals("add")) {
                        line = scanner.nextLine();
                        information = line.substring(1, line.length() - 1).split(",");
                        VM vmType = vms.get(((VMOperation) vmOperations.get(i).get(j)).getVMType());
                        int vmID = ((VMOperation) vmOperations.get(i).get(j)).getID();
                        VMInstance vmInstance = new VMInstance(vmType, vmID);
                        ServerInstance serverInstance = serverInstanceList.get(Integer.parseInt(information[0]));
                        if (vmType.isDual()) {
                            if (serverInstance.distributeDual(vmType.getCore(), vmType.getMemory())) {
                                serverInstance.addVmInstance(vmInstance);
                                vmInstance.setServerInstance(serverInstance);
                                vmInstance.setNode(2);
                                vmInstanceList.add(vmInstance);
                            } else {
                                System.out.println("error while distributing vm");
                                return;
                            }
                        } else {
                            int Node = information[1].trim().equals("A") ? 0 : 1;
                            if (Node == 0) {
                                if (serverInstance.distributeA(vmType.getCore(), vmType.getMemory())) {
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(0);
                                    vmInstanceList.add(vmInstance);
                                } else {
                                    System.out.println("error while distributing vm");
                                    return;
                                }
                            } else {
                                if (serverInstance.distributeB(vmType.getCore(), vmType.getMemory())) {
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(1);
                                    vmInstanceList.add(vmInstance);
                                } else {
                                    System.out.println("error while distributing vm");
                                    return;
                                }
                            }
                        }
                    } else {
                        boolean deleted = false;
                        for (ServerInstance serverInstance : serverInstanceList) {
                            for (VMInstance vmInstance : serverInstance.getVmInstances()) {
                                if (vmInstance.getID() == ((VMOperation) vmOperations.get(i).get(j)).ID) {
                                    if (serverInstance.delVmInstance(vmInstance)) {
                                        vmInstanceList.remove(vmInstance);
                                        deleted = true;
                                        break;
                                    }
                                }
                            }
                            if (deleted) break;
                        }
                    }
                }
            }
            System.out.println("operation judgement done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
