package com.example;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

import com.example.util.ClassLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 * 
 * @author hxb
 */
@Slf4j(topic = "c.Main")
public class App {
    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {
        List<Test> list = new Vector<>();
        int loopNumber = 10;
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
//                Test test = new Test();
//                list.add(test);
                synchronized (list) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(list).toPrintable());
                }
            }
//            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

//        t2 = new Thread(() -> {
//            LockSupport.park();
//            log.debug("=========>");
//            for (int i = 0; i < loopNumber; i++) {
//                Test test = list.get(i);
//                log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//                synchronized (test) {
//                    log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//                }
//                log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//            }
//            LockSupport.unpark(t3);
//        }, "t2");
//        t2.start();
//
//        t3 = new Thread(() -> {
//            LockSupport.park();
//            log.debug("=========>");
//            for (int i = 0; i < loopNumber; i++) {
//                Test test = list.get(i);
//                log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//                synchronized (test) {
//                    log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//                }
//                log.debug(i + "\t" + ClassLayout.parseInstance(test).toPrintable());
//            }
//        }, "t3");
//        t3.start();
//
//        t3.join();
//        log.debug(40 + "\t" + ClassLayout.parseInstance(new Test()).toPrintable());

    }
}

@Slf4j(topic = "c.Test")
class Test {
    final Object object = new Object();

    public void method1() {
        synchronized (object) {
            log.debug("方法一");
            method2();
        }
    }

    public void method2() {
        synchronized (object) {
            log.debug("方法二");
        }
    }
}
