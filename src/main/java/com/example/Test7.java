package com.example;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子引用
 */
@Slf4j(topic = "c.Test8")
public class Test7 {

    public static void main(String[] args) {
        DecimalAccount account = new DecimalAccountCas(new BigDecimal("10000"));
        // 测试结果
        DecimalAccount.demo(account);
    }
}

class DecimalAccountCas implements DecimalAccount {
    private final AtomicReference<BigDecimal> balance;

    /**
     * 构造函数传递对象
     *
     * @param bigDecimal 参数
     */
    public DecimalAccountCas(BigDecimal bigDecimal) {
        this.balance = new AtomicReference<>(bigDecimal);
    }

    @Override
    public BigDecimal getBalance() {
        // 获取余额
        return balance.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        // 实现原理
//        while (true) {
//            // 获取之前的参数
//            BigDecimal prev = balance.get();
//            // 修改参数的结果
//            BigDecimal next = prev.subtract(amount);
//            // 判断获取的之前参数是否变更，变更则表示被其他线程更新，再次进入循环进行判断修改，相同则表示没有被其他线程更新，可以直接设置，原子操作
//            if (balance.compareAndSet(prev, next)) {
//                break;
//            }
//        }
        // 更新并且摄值
        balance.updateAndGet(p -> p.subtract(amount));
    }
}


