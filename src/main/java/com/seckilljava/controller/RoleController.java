package com.seckilljava.controller;


import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.User;
import com.seckilljava.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-12
 */
@RestController
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/roles")
    public Result getRoleList() {

        return Result.succ(200, "查询成功", roleService.list());
    }
    /*

    @DeleteMapping("/roles/{id}/rights/{rightId}")
    public Result deleteRole(@PathVariable(name = "id") Long id,
                             @PathVariable(name = "rightId") Long rightId){
        roleService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }

    @DeleteMapping("/roles/{id}/rights/{rightId}")
    public Result deleteUser(@PathVariable(name = "id") Long id,
                             @PathVariable(name = "rightId") Long rightId){
        roleService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }

     */





}
