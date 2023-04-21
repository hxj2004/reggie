package com.example.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.OrderDetailMapper;
import com.example.POJO.OrderDetail;
import com.example.Service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImp extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
