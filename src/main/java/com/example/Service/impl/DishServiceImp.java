package com.example.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.DTO.DishDto;
import com.example.Mapper.DishMapper;
import com.example.POJO.Dish;
import com.example.POJO.DishFlavor;
import com.example.Service.DishFlavorService;
import com.example.Service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto){
        //保存dish的数据
        this.save(dishDto);
        Long dishid = dishDto.getId();
        //改掉dishFlavor的id，然后保存dishFlavor
        List<DishFlavor> dishFlavors = dishDto.getFlavors();

        dishFlavors = dishFlavors.stream().map((item) ->{
            item.setDishId(dishid);
            return item;
                }).collect(Collectors.toList());
        //返回
        dishFlavorService.saveBatch(dishFlavors);
    }

    //根据id查询菜品信息和对应的分类
    @Override
    @Transactional
    public DishDto getByIdWidthFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        //查询分类数据：创建分类的条件格式
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);


        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //对于dish数据保存
        this.updateById(dishDto);
        //对于dishflavor的数据进行删除保存的方法
        //删除要修改菜品的id
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //菜品id，和dishDto的id相同
        lambdaQueryWrapper.eq(dishDto != null, DishFlavor::getDishId,dishDto.getId());
        //删除id对应口味
        dishFlavorService.remove(lambdaQueryWrapper);
        //添加对应id的对应口味
        //小细节为保存dishflavor的数据需要获取到dish的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }


}
