package com.seckilljava.service;

import cn.hutool.db.sql.Order;
import com.seckilljava.entity.Good;
import com.seckilljava.entity.Orders;
import com.seckilljava.util.RandomUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author husky
 * @version 1.0
 * @date 2021/1/2 14:46
 */
@Service
public class SeckillProductService {
    @Autowired
    UserService userService;

    @Autowired
    OrdersService ordersService;

    @Autowired
    GoodService goodService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    SeckillProductService seckillProductService;

    @Autowired
    RedisTemplate redisTemplate;
    public void seckillProduct(Orders order){
        System.out.println("66666666666666666666666666");
        String key = order.getUserid().toString();
        order.setOrderid(RandomUtil.getOrderId());//随机生成唯一订单编号
        Good good = goodService.getById(order.getGoodid());
        order.setPrice(seckillService.getById(order.getId()).getPrice());
        order.setId(null);
        if(good.getNumber()<order.getNumber()){
            redisTemplate.boundValueOps(key).set("-1");
        }else{
            good.setNumber(good.getNumber()-order.getNumber());
            goodService.updateById(good);
            ordersService.save(order);
            redisTemplate.boundValueOps(key).set("-1");
        }
    }
}
