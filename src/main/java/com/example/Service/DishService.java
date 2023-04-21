package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.DTO.DishDto;
import com.example.POJO.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的分类
    public DishDto getByIdWidthFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
