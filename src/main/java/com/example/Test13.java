package com.example;

import com.example.util.Common;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * <h1>原子累加器</h1>
 * <li>LongAdder</li>
 * <li>DoubleAdder</li>
 * <p>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test13")
public class Test13 {

    public static void main(String[] args) throws InterruptedException {
        // 性能对比
        // 原子整型
        Common.demo(AtomicLong::new, AtomicLong::incrementAndGet);
        // 原子累加器
        Common.demo(LongAdder::new, (adder) -> adder.add(1));
    }
}