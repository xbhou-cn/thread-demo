package com.example;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ABA问题，无法判断变量是否被其他线程修改，一般情况下不影响业务
 * <p>
 * AtomicMarkableReference可以解决ABA问题,通过设置标识
 * <p>
 * 不关心<b>引用变量修改了几次</b>，单纯<b>关心是否被修改</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Text8")
public class Test10 {

    public static void main(String[] args) throws InterruptedException {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        // 参数2 可以看作一个标记，标识垃圾袋装满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, Boolean.TRUE);
        log.debug("main start ...");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());
        // 保洁阿姨已经更换了垃圾袋
        new Thread(() -> {
            log.debug("start ....");
            bag.setDesc("空垃圾袋");
            log.debug("换了嘛？ {}", ref.compareAndSet(prev, bag, true, false));
            log.debug(bag.toString());
        }, "保洁阿姨").start();

        TimeUnit.SECONDS.sleep(1);
        log.debug("想换一只新的垃圾袋？");
        // 尝试更换空的垃圾袋, 需要传期望标识，以及修改后的标识
        log.debug("换了嘛？ {}", ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false));
        log.debug(ref.getReference().toString());
    }
}

@AllArgsConstructor
@ToString
@Data
class GarbageBag {
    private String desc;
}
