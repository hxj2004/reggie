package com.example.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.CategoryMapper;
import com.example.POJO.Category;
import com.example.POJO.Dish;
import com.example.Service.CategoryService;
import com.example.Service.DishService;
import com.example.common.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImp extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    //根据id删除分类，删除之前分类判断

    @Override
    public void remove(Long id) {
        //查询是否关联菜品，如果关联直接抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1  = new LambdaQueryWrapper<>();
        //添加条件查询，根据id进行查询
        dishLambdaQueryWrapper1.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper1);
        //查询是否关联套餐，如果关联直接抛出异常

        if(count1 != 0){
            throw new CustomException("当前分类关联菜品");
        }

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper2  = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper2.eq(Dish::getCategoryId,id);
        int count2 = dishService.count(dishLambdaQueryWrapper2);

        if(count2 != 0){
            throw new CustomException("当前分类关联套餐");
        }
        //正常删除分类
        super.removeById(id);
    }
}
