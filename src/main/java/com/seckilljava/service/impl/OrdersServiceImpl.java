package com.seckilljava.service.impl;

import com.seckilljava.entity.Orders;
import com.seckilljava.mapper.OrdersMapper;
import com.seckilljava.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author husky
 * @since 2020-12-22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

}
