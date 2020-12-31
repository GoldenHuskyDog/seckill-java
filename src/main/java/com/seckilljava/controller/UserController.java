package com.seckilljava.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Role;
import com.seckilljava.entity.User;
import com.seckilljava.service.RoleService;
import com.seckilljava.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-11
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequiresAuthentication
    @RequiresRoles("admin")
    @GetMapping("/users")
    public Result getUserList(@RequestParam(name = "query") String query,
                       @RequestParam(name = "pagenum",defaultValue = "1") Integer pagenum,
                       @RequestParam(name = "pagesize", defaultValue = "5") Integer pagesize){

        Page page = new Page(pagenum, pagesize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(!query.isEmpty())queryWrapper.like("username","%"+query+"%");
        queryWrapper.orderByAsc("id");
        IPage pageData = userService.page(page, queryWrapper);
        return Result.succ(200,"查询成功",pageData.getRecords());
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable(name = "id") Long id){
        User user = userService.getById(id);
        return Result.succ(200,"查询成功",user);
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @PutMapping("/users/{id}")
    public Result editUser(@PathVariable(name = "id") Long id,
                           @RequestBody User user){
        User NewUser = userService.getById(id);
        NewUser.setEmail(user.getEmail());
        NewUser.setMobile(user.getMobile());
        userService.updateById(NewUser);
        return Result.succ(200,"修改成功",null);
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @DeleteMapping("/users/{id}")
    public Result deleteUser(@PathVariable(name = "id") Long id){
        userService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @PostMapping("/users")
    public Result addUser(@RequestBody User user){
        user.setRolename("user");
        user.setRoleid(3L);
        user.setPassword(SecureUtil.md5(user.getPassword()));
        userService.save(user);
        return Result.succ(200,"添加成功",null);
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @PutMapping("/users/{id}/role/{roleid}")
    public Result editRole(@PathVariable(name = "id") Long id,
                           @PathVariable(name = "roleid") Integer roleid){
        User user = userService.getById(id);
        user.setRolename(roleService.list().get(roleid-1).getRolename());
        userService.updateById(user);
        return Result.succ(200,"修改成功",null);
    }
    @RequiresAuthentication
    @RequiresRoles("admin")
    @PutMapping("/users/{id}/state/{state}")
    public Result editState(@PathVariable(name = "id") Long id){
        User user = userService.getById(id);
        user.setState(!user.getState());
        userService.updateById(user);
        return Result.succ(200,"修改成功",null);
    }

}
