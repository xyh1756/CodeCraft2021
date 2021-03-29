package com.huawei.java.main;

import com.huawei.java.model.Server;
import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VM;
import com.huawei.java.model.VMInstance;
import com.huawei.java.operation.*;
import com.huawei.java.utils.FileUtil;
import com.huawei.java.utils.OutputUtil;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        long startTime=System.currentTimeMillis();
        // 加载输入数据
        FileUtil file = new FileUtil("code/CodeCraft-2021/data/training-1.txt");
        List<Server> serverList = file.getServerList();
        Collections.sort(serverList);
        Map<String, VM> vms = file.getVms();
        List<List<Operation>> VMOperations = file.getOperations();
        List<ServerInstance> serverInstanceList = new ArrayList<>(10000);
        List<VMInstance> vmInstanceList = new ArrayList<>(30000);
//        List<List<Operation>> serverOperations = new ArrayList<>();
        List<List<Operation>> buyServerOperations = new ArrayList<>(1600);
        List<List<Operation>> distributeServerOperations = new ArrayList<>(1600);
        List<List<Operation>> migrateServerOperations = new ArrayList<>(1600);
//        List<List<Operation>> deleteVMOperations = new ArrayList<>();
        int buyServerAmount = 0;

        // 执行虚拟机创建、删除操作
        for (List<Operation> vmOperationsDaily : VMOperations) {
            List<Operation> serverOperationsDaily = new ArrayList<>();
//            List<Operation> deleteVMOperationsDaily = new ArrayList<>();
            List<Operation> migrateServerOperationsDaily = new ArrayList<>();
            List<ServerInstance> serverInstanceListVMPriority = new ArrayList<>(serverInstanceList); // 按照服务器实例剩余虚拟机占用资源升序
            List<ServerInstance> serverInstanceListResourcePriority = new ArrayList<>(serverInstanceList); // 按照服务器实例剩余资源升序
            serverInstanceListResourcePriority.sort(Comparator.comparingInt(ServerInstance::getTotalResource));
            Collections.sort(serverInstanceListVMPriority);


            // 迁移虚拟机
            int migrateAmount = 0;
            int migrateAmountLimit = vmInstanceList.size() / 200;
            for (int i = 0; migrateAmount < migrateAmountLimit && i < serverInstanceListVMPriority.size(); i++) {
                List<VMInstance> vmInstancesToMigrate = new ArrayList<>(serverInstanceListVMPriority.get(i).getVmInstances());
                for (VMInstance vmInstance : vmInstancesToMigrate) {
                    boolean migrated = false;
                    for (int j = 0; migrateAmount < migrateAmountLimit && j < serverInstanceListResourcePriority.size(); j++) {
                        if (serverInstanceListVMPriority.get(i) == serverInstanceListResourcePriority.get(j)) continue;
                        if (vmInstance.getNode() == 2) {
                            if (serverInstanceListResourcePriority.get(j).distributeDual(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                                serverInstanceListResourcePriority.get(j).addVmInstance(vmInstance);
                                vmInstance.setServerInstance(serverInstanceListResourcePriority.get(j));
                                migrateServerOperationsDaily.add(new MigrateServerOperation(vmInstance.getID(), serverInstanceListVMPriority.get(i).getID(), serverInstanceListResourcePriority.get(j).getID(), 2));
                                serverInstanceListVMPriority.get(i).delVmInstance(vmInstance);
                                migrateAmount++;
                                migrated = true;
                                break;
                            }
                        } else if (serverInstanceListResourcePriority.get(j).distributeA(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                            serverInstanceListResourcePriority.get(j).addVmInstance(vmInstance);
                            vmInstance.setServerInstance(serverInstanceListResourcePriority.get(j));
                            migrateServerOperationsDaily.add(new MigrateServerOperation(vmInstance.getID(), serverInstanceListVMPriority.get(i).getID(), serverInstanceListResourcePriority.get(j).getID(), 0));
                            serverInstanceListVMPriority.get(i).delVmInstance(vmInstance);
                            vmInstance.setNode(0);
                            migrateAmount++;
                            migrated = true;
                            break;
                        } else if (serverInstanceListResourcePriority.get(j).distributeB(vmInstance.getVmType().getCore(), vmInstance.getVmType().getMemory())) {
                            serverInstanceListResourcePriority.get(j).addVmInstance(vmInstance);
                            vmInstance.setServerInstance(serverInstanceListResourcePriority.get(j));
                            migrateServerOperationsDaily.add(new MigrateServerOperation(vmInstance.getID(), serverInstanceListVMPriority.get(i).getID(), serverInstanceListResourcePriority.get(j).getID(), 1));
                            serverInstanceListVMPriority.get(i).delVmInstance(vmInstance);
                            vmInstance.setNode(1);
                            migrateAmount++;
                            migrated = true;
                            break;
                        }
                    }
                    if (!migrated) break;
                }
                if (migrateAmount % 10 == 2) {
                    serverInstanceListResourcePriority.sort(Comparator.comparingInt(ServerInstance::getTotalResource));
                }
            }

            for (Operation operation : vmOperationsDaily) {
                VMOperation vmoperation = (VMOperation) operation;

                // 每个建立虚拟机操作之前排序
                serverInstanceList.sort(Comparator.comparingInt(ServerInstance::getTotalResource));

                // 建立虚拟机
                if (vmoperation.type.equals("add")) {
                    boolean distributed = false;
                    VM vmNeeded = vms.get(vmoperation.VMType);
                    // 双节点虚拟机
                    if (vmNeeded.isDual()) {
                        for (ServerInstance serverInstance : serverInstanceList) {
                            if (serverInstance.distributeDual(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                serverInstance.addVmInstance(vmInstance);
                                vmInstance.setServerInstance(serverInstance);
                                vmInstance.setNode(2);
                                serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 2));
                                vmInstanceList.add(vmInstance);
                                distributed = true;
                                break;
                            }
                        }
                        // 当前服务器资源不足，购买新服务器
                        if (!distributed) {
                            for (Server server : serverList) {
                                if (vmNeeded.getCore() - vmNeeded.getMemory() >= 55 && server.isEnoughDual(vmNeeded.getCore() + 37, vmNeeded.getMemory()) ||
                                        vmNeeded.getMemory() - vmNeeded.getCore() >= 55 && server.isEnoughDual(vmNeeded.getCore(), vmNeeded.getMemory() + 37)||
                                        Math.abs(vmNeeded.getCore() - vmNeeded.getMemory()) < 55 && server.isEnoughDual(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                    ServerInstance serverInstance = new ServerInstance(server);
                                    serverInstance.distributeDual(vmNeeded.getCore(), vmNeeded.getMemory());
                                    serverInstanceList.add(serverInstance);
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(2);
                                    serverOperationsDaily.add(new BuyServerOperation(server, 1, serverInstance));
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 2));
                                    vmInstanceList.add(vmInstance);
                                    distributed = true;
                                    break;
                                }
                            }
                        }
                        // 虚拟机需要资源极多，分配失败，进行新一轮分配，去掉限制
                        if (!distributed) {
                            for (Server server : serverList) {
                                if (server.isEnoughDual(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                    ServerInstance serverInstance = new ServerInstance(server);
                                    serverInstance.distributeDual(vmNeeded.getCore(), vmNeeded.getMemory());
                                    serverInstanceList.add(serverInstance);
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(2);
                                    serverOperationsDaily.add(new BuyServerOperation(server, 1, serverInstance));
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 2));
                                    vmInstanceList.add(vmInstance);
                                    break;
                                }
                            }
                        }
                    }
                    // 单节点虚拟机
                    else {
                        for (ServerInstance serverInstance : serverInstanceList) {
                            if (!(serverInstance.getALeftCore() - vmNeeded.getCore() > 62 && serverInstance.getALeftMemory() - vmNeeded.getMemory() <= 19) &&
                                    serverInstance.distributeA(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                serverInstance.addVmInstance(vmInstance);
                                vmInstance.setServerInstance(serverInstance);
                                vmInstance.setNode(0);
                                serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 0));
                                vmInstanceList.add(vmInstance);
                                distributed = true;
                                break;
                            } else if (serverInstance.distributeB(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                serverInstance.addVmInstance(vmInstance);
                                vmInstance.setServerInstance(serverInstance);
                                vmInstance.setNode(1);
                                serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 1));
                                vmInstanceList.add(vmInstance);
                                distributed = true;
                                break;
                            }
                        }
                        if (!distributed) {
                            for (ServerInstance serverInstance : serverInstanceList) {
                                if (serverInstance.distributeA(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(0);
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 0));
                                    vmInstanceList.add(vmInstance);
                                    distributed = true;
                                    break;
                                } else if (serverInstance.distributeB(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(1);
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 1));
                                    vmInstanceList.add(vmInstance);
                                    distributed = true;
                                    break;
                                }
                            }
                        }
                        // 当前服务器资源不足，购买新服务器
                        if (!distributed) {
                            for (Server server : serverList) {
                                if (vmNeeded.getCore() - vmNeeded.getMemory() >= 14 && server.isEnough(vmNeeded.getCore() + 38, vmNeeded.getMemory()) ||
                                        vmNeeded.getMemory() - vmNeeded.getCore() >= 14 && server.isEnough(vmNeeded.getCore(), vmNeeded.getMemory() + 38) ||
                                        Math.abs(vmNeeded.getCore() - vmNeeded.getMemory()) < 14 && server.isEnough(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                    ServerInstance serverInstance = new ServerInstance(server);
                                    serverInstance.distributeA(vmNeeded.getCore(), vmNeeded.getMemory());
                                    serverInstanceList.add(serverInstance);
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    vmInstance.setServerInstance(serverInstance);
                                    vmInstance.setNode(0);
                                    serverOperationsDaily.add(new BuyServerOperation(server, 1, serverInstance));
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 0));
                                    vmInstanceList.add(vmInstance);
                                    break;
                                }
                            }
                        }
                    }
                }
                // 删除虚拟机
                else {
                    for (VMInstance vmInstance : vmInstanceList) {
                        if (vmInstance.getID() == vmoperation.ID) {
                            if (vmInstance.getServerInstance().delVmInstance(vmInstance)) {
                                vmInstanceList.remove(vmInstance);
                                break;
                            }
                        }
                    }
                }
            }
            // 整理serverOperationsDaily，分配ID
//            List<Operation> buyServerOperationsDaily = new ArrayList<>();
            List<Operation> distributeServerOperationDaily = new ArrayList<>();
            List<Operation> buyServerOperationsGeneralize = new ArrayList<>();
            for (Operation serverOperation : serverOperationsDaily) {
                if (serverOperation instanceof BuyServerOperation) {
//                    buyServerOperationsDaily.add(serverOperation);
                    boolean added = false;
                    for (Operation operation : buyServerOperationsGeneralize) {
                        BuyServerOperation buyServerOperation = (BuyServerOperation) operation;
                        if (buyServerOperation.ServerType.getType().equals(((BuyServerOperation) serverOperation).ServerType.getType())) {
                            buyServerOperation.number += 1;
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        buyServerOperationsGeneralize.add(new BuyServerOperation(((BuyServerOperation) serverOperation).ServerType, 1, null));
                    }
                } else {
                    distributeServerOperationDaily.add(serverOperation);
                }
            }
            // 递增分配服务器实例ID
            for (Operation buyServerOperationOne : buyServerOperationsGeneralize) {
                for (Operation serverOperation : serverOperationsDaily) {
                    if (serverOperation instanceof BuyServerOperation && ((BuyServerOperation) buyServerOperationOne).ServerType == ((BuyServerOperation) serverOperation).ServerType) {
                        ((BuyServerOperation) serverOperation).serverInstance.setID(buyServerAmount);
                        buyServerAmount++;
                    }
                }
            }

            migrateServerOperations.add(migrateServerOperationsDaily);
//            serverOperations.add(serverOperationsDaily);
            buyServerOperations.add(buyServerOperationsGeneralize);
            distributeServerOperations.add(distributeServerOperationDaily);
//            deleteVMOperations.add(deleteVMOperationsDaily);
        }
        // 输出结果
        try {
            OutputUtil outputUtil = new OutputUtil(buyServerOperations, distributeServerOperations, migrateServerOperations, "code/CodeCraft-2021/data/output.txt");
            outputUtil.OutPut();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.flush();
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}