package com.seckilljava.service.impl;

import com.seckilljava.entity.Menu;
import com.seckilljava.mapper.MenuMapper;
import com.seckilljava.service.MenuService;
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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

}
