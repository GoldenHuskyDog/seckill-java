package com.seckilljava.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Good;
import com.seckilljava.entity.Orders;
import com.seckilljava.entity.Seckill;
import com.seckilljava.entity.User;
import com.seckilljava.service.GoodService;
import com.seckilljava.service.OrdersService;
import com.seckilljava.service.SeckillService;
import com.seckilljava.service.UserService;
import com.seckilljava.shiro.AccountProfile;
import com.seckilljava.util.JwtUtils;
import com.seckilljava.util.RandomUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author husky
 * @version 1.0
 * @date 2020/12/22 14:37
 */
@RestController
public class OrderController {
    @Autowired
    UserService userService;

    @Autowired
    OrdersService ordersService;

    @Autowired
    GoodService goodService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate redisTemplate;


    @RequiresRoles("user")
    @RequiresAuthentication
    @PostMapping("/order")
    public Result addOrder(@RequestBody Orders order) throws InterruptedException {
        String key = String.valueOf(order.getUserid()) + '-' + order.getGoodid() + "-" + order.getTime();
        redisTemplate.opsForValue().set(key,"0");
        rabbitTemplate.convertAndSend("seckill.direct","order",order);
        while(redisTemplate.hasKey(key)){
            String value = (String) redisTemplate.opsForValue().get(key);
            if(value.equals("1")){
                redisTemplate.delete(key);
                return Result.succ(200,"抢购成功",null);
            }else if(value.equals("-1")){
                redisTemplate.delete(key);
                return Result.fail(210,"抢购失败",null);
            }
        }
        redisTemplate.delete(order);
        return Result.fail(210,"抢购失败",null);
    }

    @RabbitListener(queues = "seckill.queue")
    public void receive(Orders order){
        String key = String.valueOf(order.getUserid()) + '-' + order.getGoodid() + "-" + order.getTime();
        order.setOrderid(RandomUtil.getOrderId());//随机生成唯一订单编号
        Good good = goodService.getById(order.getGoodid());
        order.setPrice(seckillService.getById(order.getId()).getPrice());
        order.setId(null);
        if(good.getNumber()<order.getNumber()){
            redisTemplate.opsForValue().set(key,"-1");
        }else{
            good.setNumber(good.getNumber()-order.getNumber());
            goodService.updateById(good);
            ordersService.save(order);
            redisTemplate.opsForValue().set(key,"1");
        }
    }

    @RequiresRoles("user")
    @RequiresAuthentication
    @GetMapping("/order")
    public Result getOrder() {
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        Long userid = accountProfile.getId();
        List<Orders> orders = ordersService.list(new QueryWrapper<Orders>().eq("userid", userid));
        List<Object> ordersList = new ArrayList<>();
        User user = userService.getById(userid);
        for (int i = 0; i < orders.size(); i++) {
            Orders order = orders.get(i);
            ordersList.add(MapUtil.builder()
                    .put("orderid", order.getOrderid())
                    .put("username", user.getUsername())
                    .put("goodname", goodService.getById(order.getGoodid()).getName())
                    .put("number", order.getNumber())
                    .put("price", order.getPrice())
                    .put("time", order.getTime())
                    .put("path", user.getPath())
                    .put("sum", order.getPrice() * order.getNumber())
                    .map());
        }
        return Result.succ(200, "添加成功", ordersList);
    }

}
