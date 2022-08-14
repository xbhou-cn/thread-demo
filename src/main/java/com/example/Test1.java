package com.example;

/**
 * @author hxb
 */
public class Test1 {
    private static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        synchronized (LOCK) {
            LOCK.wait();
        }
    }
}
