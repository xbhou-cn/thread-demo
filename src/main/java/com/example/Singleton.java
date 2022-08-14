package com.example;

/**
 * 使用final避免被继承重写
 *
 * @author hxb
 */
public final class Singleton {

    private static volatile Singleton INSTANCE = null;

    /**
     * 私有化构造函数
     */
    private Singleton() {
    }

    public static Singleton newInstance() {
        // 双重检测，因为INSTANCE没有在synchronized中，所以需要volatile保持可见性
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }

}


