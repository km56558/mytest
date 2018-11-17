package com.pinyougou.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SeckillTask {

    @Scheduled(cron = "* * * * * ?")
    public void test(){
        System.out.println("每秒更新");
    }
}
