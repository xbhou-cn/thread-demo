package com.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Common {
    public static final int SIZE = 10000;

    public static void main(String[] args) {
        demo(() -> new int[10], arr -> arr.length, (arr, index) -> arr[index]++, arr -> System.out.println(Arrays.toString(arr)));
    }

    /**
     * @param arraySupplier  提供数组，可以是线程安全或者不安全的数组
     * @param lengthFunction 获取数组长度方法
     * @param putConsumer    自增方法，回传array，index
     * @param printConsumer  打印数组的方法
     * @param <T>            泛型
     */
    public static <T> void demo(Supplier<T> arraySupplier, Function<T, Integer> lengthFunction,
                                BiConsumer<T, Integer> putConsumer, Consumer<T> printConsumer) {
        // 线程集合
        List<Thread> ts = new ArrayList<>();
        // 创建数组的方法
        T array = arraySupplier.get();
        // 获取数组长度的方法
        int length = lengthFunction.apply(array);
        // 根据数组的长度创建线程
        for (int i = 0; i < length; i++) {
            ts.add(new Thread(() -> {
                // 批量操作数组
                for (int j = 0; j < SIZE; j++) {
                    // 操作数组的方法
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        // 启动线程
        ts.forEach(Thread::start);
        // 等待线程执行结束
        ts.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 打印数组的方法
        printConsumer.accept(array);
    }
}
