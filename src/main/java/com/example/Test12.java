package com.example;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * <h1>字段更新器</h1>
 * <li>AtomicReferenceFieldUpdater</li>
 * <li>AtomicIntegerFieldUpdater</li>
 * <li>AtomicLongFieldUpdater</li>
 * <p>
 * <b>可以针对对象的某个域（Field）进行原子操作，只能配合volatile修饰的字段使用，并且不能使用private修饰，否则会出现异常</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test12")
public class Test12 {

    public static void main(String[] args) throws InterruptedException {
        Student stu = new Student();
        // 需要修改的字段不能用private修饰，必须使用volatile修饰
        AtomicReferenceFieldUpdater<Student, String> updater =
                AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        log.debug("修改成功？ {}", updater.compareAndSet(stu, null, "王二"));
        log.debug("{}", stu);
    }
}

@Data
@ToString
class Student {
    volatile String name;
}