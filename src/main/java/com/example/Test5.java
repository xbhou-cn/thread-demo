package com.example;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 主程序
 * 
 * @author hxb
 */
public class Test5 {
    public static void main(String[] args) throws InterruptedException {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                queue.set(new Message(id, "消息" + id));
            }, "生产者" + i).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                while (true) {
                    queue.get();
                }
            }, "消费者").start();
        }
    }
}

/**
 * 模拟消息队列，多对多
 */
@RequiredArgsConstructor
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    /**
     * 队列最大储存数量
     */
    private final int size;

    private static final LinkedList<Message> QUEUE = new LinkedList<>();

    /**
     * 获取内容
     * 
     * @throws InterruptedException
     */
    public Message get() {
        synchronized (QUEUE) {
            // 如果链表中没有内容则等待
            while (QUEUE.isEmpty()) {
                try {
                    QUEUE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 按顺序取出内容
            Message first = QUEUE.removeFirst();
            log.debug("取出消息,id:{},内容：{}", first.getId(), first.getMessage());
            // 唤醒等待传输数据的线程
            QUEUE.notifyAll();
            return first;
        }
    }

    /**
     * 设置内容
     * 
     * @throws InterruptedException
     */
    public void set(Message message) {
        synchronized (QUEUE) {
            while (QUEUE.size() == size) {
                try {
                    log.debug("等待发送数据！");
                    QUEUE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            QUEUE.addLast(message);
            log.debug("发送消息,id:{},内容：{}", message.getId(), message.getMessage());
            // 唤醒取数据线程
            QUEUE.notifyAll();
        }
    }
}

@RequiredArgsConstructor
@Getter
class Message {
    private final int id;
    private final String message;
}