package com.seckilljava.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.seckilljava.entity.User;
import com.seckilljava.service.UserService;
import com.seckilljava.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author husky
 * @version 1.0
 * @date 2020/12/11 23:49
 */

@Configuration
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    /*@Autowired
    private RightsService rightsService;*/

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取用户名称
        AccountProfile accountProfile = (AccountProfile) principals.getPrimaryPrincipal();
       /* User user = userService.getOne(new QueryWrapper<User>().eq("id", accountProfile.getId()));
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }*/
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 设置角色
        String role = accountProfile.getRolename();

        if (role == null) {
            throw new UnknownAccountException("用户没有角色");
        }
        authorizationInfo.addRole(role);
        /*//设置权限
        Set<String> permissions = new HashSet<>();
        List<Rights> permissionslist = rightsService.list(new QueryWrapper<Rights>().eq("role",role));
        for (Rights rights :permissionslist) {
            permissions.add(rights.getPermission());
        }
        authorizationInfo.setStringPermissions(permissions);*/
        return authorizationInfo;
    }

    /**
     * 验证账号密码
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        JwtToken jwtToken = (JwtToken) token;

        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();

        User user = userService.getById(Long.valueOf(userId));

        if (user == null) {
            throw new UnknownAccountException("账户不存在");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);

        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(),
                getName());
    }
}
