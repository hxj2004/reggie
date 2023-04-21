package com.example.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.ShoppingCartMapper;
import com.example.POJO.ShoppingCart;
import com.example.Service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImp extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {
}
