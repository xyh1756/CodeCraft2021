package com.huawei.java.main;

import com.huawei.java.model.Server;
import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VM;
import com.huawei.java.model.VMInstance;
import com.huawei.java.operation.*;
import com.huawei.java.utils.FileUtil;
import com.huawei.java.utils.JudgeUtil;
import com.huawei.java.utils.OutputUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 加载输入数据
        FileUtil file = new FileUtil("code/CodeCraft-2021/data/training-1.txt");
        List<Server> serverList = file.getServerList();
        Map<String, VM> vms = file.getVms();
        List<List<Operation>> VMOperations = file.getOperations();

        // 执行虚拟机创建、删除操作
        Collections.sort(serverList);
        List<ServerInstance> serverInstanceList = new ArrayList<>();
        List<VMInstance> vmInstanceList = new ArrayList<>();
        List<List<Operation>> serverOperations = new ArrayList<>();
        List<List<Operation>> buyServerOperations = new ArrayList<>();
        List<List<Operation>> distributeServerOperation = new ArrayList<>();
        List<List<Operation>> deleteVMOperation = new ArrayList<>();
        int buyServerAmount = 0;

        for (List<Operation> vmOperationsDaily : VMOperations) {
            List<Operation> serverOperationsDaily = new ArrayList<>();
            List<Operation> deleteVMOperationsDaily = new ArrayList<>();
            for (Operation operation : vmOperationsDaily) {
                VMOperation vmoperation = (VMOperation) operation;
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
                        // 当前服务器资源不足，购买新服务器
                        if (!distributed) {
                            for (Server server : serverList) {
                                if (server.isEnough(vmNeeded.getCore(), vmNeeded.getMemory())) {
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
                    boolean deleted = false;
                    for (ServerInstance serverInstance : serverInstanceList) {
                        for (VMInstance vmInstance : serverInstance.getVmInstances()) {
                            if (vmInstance.getID() == vmoperation.ID) {
                                if (serverInstance.delVmInstance(vmInstance)) {
                                    vmInstanceList.remove(vmInstance);
                                    deleteVMOperationsDaily.add(new DeleteVMOperation(vmInstance.getID()));
                                    deleted = true;
                                    break;
                                }
                            }
                        }
                        if (deleted) break;
                    }
                }
            }
            // 整理serverOperationsDaily，分配ID
            List<Operation> buyServerOperationsDaily = new ArrayList<>();
            List<Operation> distributeServerOperationDaily = new ArrayList<>();
            List<Operation> buyServerOperationsGeneralize = new ArrayList<>();
            for (Operation serverOperation : serverOperationsDaily) {
                if (serverOperation instanceof BuyServerOperation) {
                    buyServerOperationsDaily.add(serverOperation);
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
            serverOperations.add(serverOperationsDaily);
            buyServerOperations.add(buyServerOperationsGeneralize);
            distributeServerOperation.add(distributeServerOperationDaily);
            deleteVMOperation.add(deleteVMOperationsDaily);
        }
        // 输出结果
        OutputUtil outputUtil = new OutputUtil(buyServerOperations, distributeServerOperation);
        outputUtil.OutPut();
        System.out.flush();
//        JudgeUtil judgeUtil = new JudgeUtil("code/CodeCraft-2021/data/output.txt", file.getServers(), vms, VMOperations);
//        judgeUtil.Judge();
    }
}