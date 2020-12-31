package com.seckilljava.controller;


import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Good;
import com.seckilljava.entity.Seckill;
import com.seckilljava.entity.User;
import com.seckilljava.service.GoodService;
import com.seckilljava.service.SeckillService;
import com.seckilljava.service.UserService;
import com.seckilljava.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-22
 */
@RestController
public class SeckillController {

    @Autowired
    SeckillService seckillService;
    @Autowired
    GoodService goodService;
    @Autowired
    UserService userService;

    @RequiresRoles("user")
    @RequiresAuthentication
    @GetMapping("/seckill")
    public Result getSeckillList(){

        ArrayList<Seckill> arrayList = (ArrayList<Seckill>) seckillService.list();
        ArrayList<Object> seckilllist = new ArrayList<>();
        for (Seckill seckill:arrayList) {
            Long goodid = seckill.getGoodid();
            Good good = goodService.getById(goodid);
            seckilllist.add( MapUtil.builder()
                    .put("seckillId",seckill.getId())
                    .put("goodId", seckill.getGoodid())
                    .put("img",good.getImg())
                    .put("goodName", good.getName())
                    .put("seckillPrice", seckill.getPrice())
                    .put("goodPrice",good.getPrice())
                    .map());
        }

        return Result.succ(200,"查询成功", seckilllist);
    }

    @RequiresRoles("user")
    @RequiresAuthentication
    @GetMapping("/seckillDetails")
    public Result getSeckillDetail(@RequestParam(name = "seckillID") Integer seckillid,
                                   @RequestParam(name = "goodID") Integer goodid){

        Seckill seckill = seckillService.getById(seckillid);
        Good good = goodService.getById(goodid);
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        System.out.println(accountProfile.getId());
        User user = userService.getById(accountProfile.getId());
        //System.out.println(goodid + seckillid);
        return Result.succ(200,"查询成功", MapUtil.builder()
                .put("goodName",good.getName())
                .put("startTime",seckill.getStart())
                .put("endTime",seckill.getEnd())
                .put("seckillPrice",seckill.getPrice())
                .put("goodPrice",good.getPrice())
                .put("goodPicture", good.getImg())
                .put("path",user.getPath())
                .map()
        );
    }
}
