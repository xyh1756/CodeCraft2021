### 2021华为软件精英挑战赛

-----

#### 关于队伍
* 粤港澳赛区初赛25名，复赛15名
![](https://i.bmp.ovh/imgs/2021/04/426acc60199ce5bb.png)

-----

#### 购买策略

* 服务器按照硬件成本+7/10 * 剩余天数 * 能耗成本排序，每次服务器资源不够时购买最便宜的
* 购买新服务器时，如果当前要分配的虚拟机出现需要的资源很不平衡（CPU核数远大于内存或内存远大于CPU核数）的情况，
  给购买增加惩罚机制，需要有比虚拟机要求的更多的资源才能分配。举个例子，如果虚拟机需要的资源是80、10，
  那么新购买的虚拟机就需要有100、10的资源才可满足，这样最后剩下的CPU核数至少为20，而不是0
* 如果虚拟机需要的资源极多，在惩罚机制下没有新服务器可以满足，则去掉惩罚机制开启第二轮购买

-----

#### 分配策略

* 每天的虚拟机操作以删除操作为边界，进行分段排序，占用资源大的虚拟机操作放在前面，有利于让小型虚拟机填满剩余资源
* 分配双节点虚拟机前，把服务器实例按照剩余总资源升序排序
* 分配单节点虚拟机前，把服务器实例按照单节点最高剩余资源升序排序
* 和购买策略一样，分配策略也引入了惩罚机制

-----

#### 迁移策略

* 维护两个服务器实例列表，分别按照剩余资源最多和最少排序
* 剩余资源最少的排序列表中，只包含剩余资源大于总资源1/17的。这是为了减少比较次数，对成本影响不大，但可以很大程度上提升性能
* 把剩余资源最多的服务器中全部虚拟机迁移到剩余资源最少的服务器上，如果某个服务器有一个虚拟机无法迁移，则直接跳到下个服务器
* 每个迁出的服务器虚拟机都迁移后，满足 当天迁移总量 % 3 == 2 则把保存剩余资源最少的排序列表重新排序
  （这里不是每次迁移都排序主要是效果和时间之间的权衡）

---

#### 输入输出

* 把输出结果保存到String中，一次输出，可以比频繁调用print输出有更好的性能
