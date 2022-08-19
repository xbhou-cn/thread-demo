package com.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface DecimalAccount {
    static final int LENGTH = 1000;

    /**
     * 获取余额
     *
     * @return
     */
    BigDecimal getBalance();

    /**
     * 取款
     *
     * @param amount 取款金额
     */
    void withdraw(BigDecimal amount);

    /**
     * 方法内启动1000个线程，每个线程做 -10 元操作，如果初始余额为10000 那么正确的结果应当是0
     *
     * @param account 实验对象
     */
    static void demo(DecimalAccount account) {
        // 测试线程列表
        List<Thread> ts = new ArrayList<>();
        // 创建线程保存到集合
        for (int i = 0; i < LENGTH; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        // 启动线程
        ts.forEach(Thread::start);
        // 等待所有线程执行完毕
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 获取余额
        System.out.println(account.getBalance());
    }
}
