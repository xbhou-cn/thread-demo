package com.example;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test3")
public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i < 100; i++) {
            new People().start();
        }
        TimeUnit.SECONDS.sleep(2);
        for (int id : Mailboxes.getIds()) {
            new Postman(id, "邮件" + id).start();
        }
    }
}

@Slf4j(topic = "c.People")
class People extends Thread {

    @Override
    public void run() {
        // 根据用户创建邮箱
        GuardedObjectMail guardedObject = Mailboxes.createGuardedObjectMail();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get();
        log.debug("收到信 id:{}，内容：{}", guardedObject.getId(), mail);
    }
}

@Slf4j(topic = "c.Postman")
@RequiredArgsConstructor
class Postman extends Thread {
    private final int id;
    private final String mail;

    @Override
    public void run() {
        // 获取邮箱
        GuardedObjectMail guardedObject = Mailboxes.getGuardedObjectMail(id);
        log.debug("完成送信 id:{}，内容：{}", guardedObject.getId(), mail);
        guardedObject.set(mail);
    }
}

class Mailboxes {
    private static final Map<Integer, GuardedObjectMail> BOXES = new Hashtable<>();

    private static int id = 1;

    public synchronized static int generateId() {
        return id++;
    }

    /**
     * 创建对象
     * 
     * @return
     */
    public static GuardedObjectMail createGuardedObjectMail() {
        GuardedObjectMail guardedObject = new GuardedObjectMail(generateId());
        BOXES.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    /**
     * 获取对象
     * 
     * @param id
     * @return
     */
    public static GuardedObjectMail getGuardedObjectMail(int id) {
        return BOXES.remove(id);
    }

    /**
     * 获取所有ID
     * 
     * @return
     */
    public static Set<Integer> getIds() {
        return BOXES.keySet();
    }

}

@Getter
@RequiredArgsConstructor
class GuardedObjectMail {
    // 结果
    private Object response;

    // id
    private final int id;

    // 获取结果
    public Object get() {
        return this.get(-1);
    }

    // 获取结果
    public synchronized Object get(long timeout) {
        // 开始时间
        long begin = System.currentTimeMillis();
        long passedTime = 0;
        while (response == null) {
            if (timeout >= 0 && passedTime >= timeout) {
                break;
            }
            try {
                if (timeout >= 0) {
                    this.wait(timeout - passedTime);
                } else {
                    this.wait();
                }
            } catch (InterruptedException ignore) {
            }
            passedTime = System.currentTimeMillis() - begin;
        }
        return response;
    }

    public synchronized void set(Object response) {
        this.response = response;
        this.notifyAll();
    }
}