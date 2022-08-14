package com.example;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test4")
public class Test4 {
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, String> map = new Hashtable<>();
        for (int i = 0; i < 200; i++) {
            final int id = i;
            new Thread(() -> {
                map.put(id, "测试" + id);
                log.debug("放入结果" + "测试" + id);
            }, "线程" + i).start();
        }
        TimeUnit.SECONDS.sleep(5);
        log.debug(map.keySet().toString());
        for (int id : map.keySet()) {
        
            log.debug("取得结果" + map.remove(id));
        }
        // while (iterator.hasNext()) {
        // final int id = iterator.next();
        // new Thread(() -> {
        // log.debug("取得结果" + map.remove(id));
        // }, "线程" + id).start();
        // }
    }
}
