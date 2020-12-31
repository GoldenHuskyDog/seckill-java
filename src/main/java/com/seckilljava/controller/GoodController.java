package com.seckilljava.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Good;
import com.seckilljava.service.RoleService;
import com.seckilljava.service.GoodService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-14
 */
@RestController
public class GoodController {
    @Autowired
    GoodService goodService;
    @Autowired
    RoleService roleService;

    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @GetMapping("/goods")
    public Result getGoodList(@RequestParam(name = "query") String query,
                              @RequestParam(name = "pagenum", defaultValue = "1") Integer pagenum,
                              @RequestParam(name = "pagesize", defaultValue = "5") Integer pagesize) {

        Page page = new Page(pagenum, pagesize);
        QueryWrapper<Good> queryWrapper = new QueryWrapper<>();
        if (!query.isEmpty()) queryWrapper.like("name", "%" + query + "%");
        queryWrapper.orderByAsc("id");
        IPage pageData = goodService.page(page, queryWrapper);
        return Result.succ(200, "查询成功", pageData.getRecords());
    }
    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @PostMapping("/goods")
    public Result addGood(@RequestBody Good good){
        good.setImg("xiaomi.png");
        goodService.save(good);
        return Result.succ(200,"添加成功",null);
    }
    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @DeleteMapping("/goods/{id}")
    public Result deleteGood(@PathVariable(name = "id") Long id){
        goodService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }
    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @GetMapping("/goods/{id}")
    public Result getGood(@PathVariable(name = "id") Long id){
        Good good = goodService.getById(id);
        return Result.succ(200,"查询成功", good);
    }
    @RequiresRoles(value = {"manager","admin"},logical = Logical.OR)
    @RequiresAuthentication
    @PutMapping("/goods/{id}")
    public Result editGood(@PathVariable(name = "id") Long id,
                           @RequestBody Good good){
        Good NewGood = goodService.getById(id);
        NewGood.setPrice(good.getPrice());
        NewGood.setNumber(good.getNumber());
        goodService.updateById(NewGood);
        return Result.succ(200,"修改成功",null);
    }



}
