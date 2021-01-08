package com.seckilljava.config;

import com.seckilljava.entity.Seckill;
import com.seckilljava.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author husky
 * @version 1.0
 * @date 2021/1/2 21:19
 */

@Component
public class LoadData implements CommandLineRunner {


    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SeckillService seckillService;
    @Override
    public void run(String... args) throws Exception {

        List<Seckill> seckills = seckillService.list();
        for(int i=0;i<seckills.size();i++){
            String seckillid = "seckillid:" + seckills.get(i).getId();
            String stock = String.valueOf(seckills.get(i).getStock());
            stringRedisTemplate.opsForValue().set(seckillid,stock);
        }

    }
}
