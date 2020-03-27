package com.dezheng.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SecKillGoodsTask {

    @Scheduled(cron = "0/5 * * * * ?")
    public void saveSecKillGoods() {
        System.out.println(new Date());
    }

}
