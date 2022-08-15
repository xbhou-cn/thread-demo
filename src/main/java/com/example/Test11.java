package com.example;

import com.example.util.Common;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * <h1>原子数组</h1>
 * <li>AtomicIntegerArray</li>
 * <li>AtomicLongArray</li>
 * <li>AtomicReferenceArray</li>
 * <p>
 * <b>保护数组中的元素</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Text8")
public class Test11 {

    public static void main(String[] args) throws InterruptedException {
        Common.demo(() -> new AtomicIntegerArray(10), AtomicIntegerArray::length, AtomicIntegerArray::getAndIncrement, System.out::println);
    }
}