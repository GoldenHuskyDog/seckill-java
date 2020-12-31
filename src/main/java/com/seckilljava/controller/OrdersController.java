package com.seckilljava.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Orders;
import com.seckilljava.service.OrdersService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-22
 */
@RestController
public class OrdersController {

    @Autowired
    OrdersService ordersService;


    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @GetMapping("/orders")
    public Result getOrderList(@RequestParam(name = "query") String query,
                              @RequestParam(name = "pagenum", defaultValue = "1") Integer pagenum,
                              @RequestParam(name = "pagesize", defaultValue = "5") Integer pagesize) {

        Page page = new Page(pagenum, pagesize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        if (!query.isEmpty()) queryWrapper.like("orderid", "%" + query + "%");
        queryWrapper.orderByAsc("id");
        IPage pageData = ordersService.page(page, queryWrapper);
        return Result.succ(200, "查询成功", pageData.getRecords());
    }

    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @DeleteMapping("/orders/{id}")
    public Result deleteOrder(@PathVariable(name = "id") Long id){
        ordersService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }

}
