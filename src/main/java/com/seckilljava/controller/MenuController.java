package com.seckilljava.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckilljava.common.dto.LoginDto;
import com.seckilljava.common.result.Result;
import com.seckilljava.entity.Menu;
import com.seckilljava.entity.Rights;
import com.seckilljava.entity.User;
import com.seckilljava.service.MenuService;
import com.seckilljava.service.RightsService;
import com.seckilljava.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author husky
 * @since 2020-12-12
 */
@RestController
public class MenuController {

    @Autowired
    MenuService menuService;
    @Autowired
    RightsService rightsService;

    @GetMapping("/menus")
    public Result list(){
            AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
            Long roleid = accountProfile.getRoleid();
            List<Rights> ParentRights = rightsService.list(new QueryWrapper<Rights>().eq("roleid",roleid));

            List<Object> MenuList = new ArrayList<>();
            for(int i=0;i<ParentRights.size();i++){
                Long index =ParentRights.get(i).getMenuid();
                Menu ParentMenu = menuService.getOne(new QueryWrapper<Menu>().eq("id", index));
                List<Menu> SubMenus =menuService.list((QueryWrapper) new QueryWrapper().between("id",index*10,(index+1)*10));
                List<Object> Children = new ArrayList<>();
                for(int j=0;j<SubMenus.size();j++){
                    Children.add(MapUtil.builder()
                            .put("id",SubMenus.get(j).getId())
                            .put("authName", SubMenus.get(j).getAuthname())
                            .put("path", SubMenus.get(j).getPath())
                            .put("children",null)
                            .map());
                }
                MenuList.add(MapUtil.builder()
                        .put("id", ParentMenu.getId())
                        .put("authName", ParentMenu.getAuthname())
                        .put("path",ParentMenu.getPath())
                        .put("children",Children)
                        .map());

        }
        return Result.succ(200, "success", MenuList);
    }

}
