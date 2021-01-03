package com.seckilljava.controller;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Orders;
import com.seckilljava.entity.Seckill;
import com.seckilljava.entity.User;
import com.seckilljava.service.*;
import com.seckilljava.shiro.AccountProfile;
import com.seckilljava.util.RandomUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SeckillProductService seckillProductService;


    @RequiresRoles("user")
    @RequiresAuthentication
    @GetMapping("/orderstate")
    public Result getOrderState(){
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        String key = "userid:" + accountProfile.getId();
        if(!stringRedisTemplate.hasKey(key)){
            return Result.fail(220,"抢购失败",null);
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        if(value.equals("1")){
            stringRedisTemplate.delete(key);
            String orderstr = stringRedisTemplate.opsForValue().get("order:"+ accountProfile.getId());
            JSONObject jsonObject = JSONObject.parseObject(orderstr);
            Orders order = JSON.toJavaObject(jsonObject,Orders.class);
            Seckill seckill = seckillService.getById(order.getId());
            seckill.setStock(seckill.getStock()-order.getNumber());
            order.setGoodid(seckill.getGoodid());
            order.setPrice(seckill.getPrice());
            seckillService.updateById(seckill);
            ordersService.save(order);
            stringRedisTemplate.delete("order:"+order.getUserid());
            return Result.succ(200,"抢购成功",null);
        }
        else{
            stringRedisTemplate.delete(key);
            return Result.succ(210,"抢购失败",null);
        }
    }
    @RequiresRoles("user")
    @RequiresAuthentication
    @PostMapping("/order")
    public Result addOrder(@RequestBody Orders order) throws InterruptedException {
        rabbitTemplate.convertAndSend("seckill.direct","order",order);
        return Result.succ(200,"排队中...",null);
    }

    /*@RequiresRoles("user")
    @RequiresAuthentication*/
    @PostMapping("/ordertest")
    public Result testOrder(@RequestBody Orders order) throws InterruptedException {
        order.setOrderid(RandomUtil.getOrderId());//随机生成唯一订单编号
        Seckill seckill = seckillService.getById(order.getId());
        order.setId(null);
        if(seckill.getStock()<order.getNumber()){
            return Result.fail(210,"抢购失败",null);
        }else{
            seckill.setStock(seckill.getStock()-order.getNumber());
            order.setGoodid(seckill.getGoodid());
            order.setPrice(seckill.getPrice());
            seckillService.updateById(seckill);
            ordersService.save(order);
        }
        return Result.succ(200,"抢购成功",null);
    }

    @RabbitListener(queues = "seckill.queue")
    public void receive(Orders order){
        String key = "userid:" + order.getUserid();
        if(redisTemplate.hasKey(key)){
            stringRedisTemplate.opsForValue().set(key,"-1");
            return;
        }
        order.setOrderid(RandomUtil.getOrderId());//随机生成唯一订单编号
        String seckillid= "seckillid:" + order.getId();
        Long stock = Long.valueOf(stringRedisTemplate.opsForValue().get(seckillid));
        Long ordernum = order.getNumber();
        if(stock<ordernum){
            stringRedisTemplate.opsForValue().set(key,"-1");
        }else{
            stringRedisTemplate.opsForValue().decrement(seckillid,ordernum);
            stringRedisTemplate.opsForValue().set("order:"+ order.getUserid(), JSON.toJSONString(order));
            stringRedisTemplate.opsForValue().set(key,"1");
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
