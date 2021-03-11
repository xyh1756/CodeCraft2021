package com.huawei.java.main;

import com.huawei.java.model.Server;
import com.huawei.java.model.ServerInstance;
import com.huawei.java.model.VM;
import com.huawei.java.model.VMInstance;
import com.huawei.java.operation.BuyServerOperation;
import com.huawei.java.operation.DistributeServerOperation;
import com.huawei.java.operation.Operation;
import com.huawei.java.operation.VMOperation;
import com.huawei.java.utils.FileUtil;
import com.huawei.java.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 加载输入数据
        LogUtil.printLog("开始加载输入");
        FileUtil file = new FileUtil("code/CodeCraft-2021/data/training-1.txt");
        Map<String, Server> servers = file.getServers();
        List<Server> serverList = file.getServerList();
        Map<String, VM> vms = file.getVms();
        List<List<Operation>> VMOperations = file.getOperations();
        LogUtil.printLog("加载输入完成");

        // TODO: process
        Collections.sort(serverList);
        List<ServerInstance> serverInstanceList = new ArrayList<>();
        List<VMInstance> vmInstanceList = new ArrayList<>();
        List<List<Operation>> serverOperations = new ArrayList<>();

        for (List<Operation> vmOperationsDaily : VMOperations) {
            List<Operation> serverOperationsDaily = new ArrayList<>();
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
                                serverInstance.setRunning(true);
                                vmInstance.setServerInstance(serverInstance);
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
                                    serverOperationsDaily.add(new BuyServerOperation(server, 1));
                                    ServerInstance serverInstance = new ServerInstance(server);
                                    serverInstanceList.add(serverInstance);
                                    serverInstance.distributeDual(vmNeeded.getCore(), vmNeeded.getMemory());
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    serverInstance.setRunning(true);
                                    vmInstance.setServerInstance(serverInstance);
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
                                serverInstance.setRunning(true);
                                vmInstance.setServerInstance(serverInstance);
                                serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 0));
                                vmInstanceList.add(vmInstance);
                                distributed = true;
                                break;
                            } else if (serverInstance.distributeB(vmNeeded.getCore(), vmNeeded.getMemory())) {
                                VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                serverInstance.addVmInstance(vmInstance);
                                serverInstance.setRunning(true);
                                vmInstance.setServerInstance(serverInstance);
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
                                    serverOperationsDaily.add(new BuyServerOperation(server, 1));
                                    ServerInstance serverInstance = new ServerInstance(server);
                                    serverInstanceList.add(serverInstance);
                                    serverInstance.distributeA(vmNeeded.getCore(), vmNeeded.getMemory());
                                    VMInstance vmInstance = new VMInstance(vmNeeded, vmoperation.ID);
                                    serverInstance.addVmInstance(vmInstance);
                                    serverInstance.setRunning(true);
                                    vmInstance.setServerInstance(serverInstance);
                                    serverOperationsDaily.add(new DistributeServerOperation(vmInstance, serverInstance, 0));
                                    vmInstanceList.add(vmInstance);
                                    break;
                                }
                            }
                        }
                    }

                }
                // todo: 删除虚拟机
                else {

                }
                // todo: 整理serverOperationsDaily，分配ID
            }
        }
        // TODO: write standard output
        // TODO: System.out.flush()
    }
}