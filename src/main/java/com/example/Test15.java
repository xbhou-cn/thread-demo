package com.example;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>Unsafe</h1>
 * <b>使用Unsafe实现cas操作</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test15")
public class Test15 {

    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        log.debug("unsafe：{}", unsafe);
        // 1、获取域的偏移地址
        long id = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long name = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        // 2、执行cas操作
        Teacher t = new Teacher();
        unsafe.compareAndSwapInt(t, id, 0, 12);
        unsafe.compareAndSwapObject(t, name, null, "张三");

        // 3、验证
        log.debug("Teacher：{}", t);
    }
}

@Data
class Teacher {
    private int id;
    private String name;
}