package com.seckilljava.service.impl;

import com.seckilljava.entity.Role;
import com.seckilljava.mapper.RoleMapper;
import com.seckilljava.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author husky
 * @since 2020-12-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
