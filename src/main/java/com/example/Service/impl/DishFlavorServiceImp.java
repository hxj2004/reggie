package com.example.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.DishFlavorMapper;
import com.example.POJO.DishFlavor;
import com.example.Service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImp extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
