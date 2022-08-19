package com.example;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * <h1>Unsafe</h1>
 * <b>使用Unsafe实现cas操作</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test16")
public class Test16 {

    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        DecimalAccount.demo(new MyAtomicInteger(1000 * 10));
    }
}

class MyAtomicInteger implements DecimalAccount {
    private volatile int value;
    private static final Unsafe UNSAFE;
    private static final long UNSAFE_OFFSET;

    public MyAtomicInteger() {
        new MyAtomicInteger(0);
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    /**
     * 返值
     *
     * @return int
     */
    public int getValue() {
        return value;
    }

    /**
     * 原子操作，减去某数
     *
     * @param amount 减数
     */
    public void decrement(int amount) {
        while (true) {
            int prev = this.value;
            int next = prev - amount;
            if (UNSAFE.compareAndSwapInt(this, UNSAFE_OFFSET, prev, next)) {
                break;
            }
        }
    }

    /**
     * 获得Unsafe方法
     *
     * @return Unsafe
     */
    static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static {
        // 初始化对象
        UNSAFE = getUnsafe();
        // 获取
        try {
            UNSAFE_OFFSET = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(getValue());
    }

    @Override
    public void withdraw(BigDecimal amount) {
        decrement(amount.intValue());
    }
}