package com.seckilljava.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckilljava.common.dto.LoginDto;
import com.seckilljava.entity.User;
import com.seckilljava.service.UserService;
import com.seckilljava.common.result.Result;
import com.seckilljava.shiro.AccountProfile;
import com.seckilljava.util.JwtUtils;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-11
 */
@RestController
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public Object login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) throws AuthenticationException {

        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        if(user == null){
            return Result.fail("用户不存在或密码不正确");
        }

        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("用户不存在或密码不正确");
        }
        String jwt = jwtUtils.generateToken(String.valueOf(user.getId()));

        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        return Result.succ(200, "success", MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("roleid", user.getRoleid())
                .put("rolename",user.getRolename())
                .map());
    }

    @GetMapping("/logout")
    @RequiresAuthentication
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.succ("退出成功");
    }

    @RequiresRoles("user")
    @RequiresAuthentication
    @GetMapping("/user")
    public Result getUser(){
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        User user = userService.getById(accountProfile.getId());
        return Result.succ(200,"查询成功",user);
    }
    @RequiresRoles("user")
    @RequiresAuthentication
    @PutMapping("/user")
    public Result editUser(@RequestBody User user){
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        User NewUser = userService.getById(accountProfile.getId());
        NewUser.setEmail(user.getEmail());
        NewUser.setMobile(user.getMobile());
        NewUser.setPath(user.getPath());
        userService.updateById(NewUser);
        return Result.succ(200,"修改成功",null);
    }

    @RequiresRoles("user")
    @RequiresAuthentication
    @PutMapping("/password")
    public Result editPassword(@RequestBody HashMap<String,String> map){
        String password = map.get("password");
        String password1 = map.get("password1");
        String password2 = map.get("password2");
        System.out.println(password);
        System.out.println(password1);
        System.out.println(password2);
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        User user = userService.getById(accountProfile.getId());
        if(!user.getPassword().equals(SecureUtil.md5(password))){
            return Result.fail(210,"原密码错误",null);
        }else if(!password1.equals(password2)){
            return Result.fail(210,"新密码不一致",null);
        }else{
            user.setPassword(SecureUtil.md5(password2));
            userService.updateById(user);
            return Result.succ(200,"修改成功",null);
        }
    }
}
