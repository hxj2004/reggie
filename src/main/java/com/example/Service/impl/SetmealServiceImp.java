package com.example.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.DTO.SetmealDto;
import com.example.Mapper.EmployeeMapper;
import com.example.Mapper.SetmealMapper;
import com.example.POJO.Employee;
import com.example.POJO.Setmeal;
import com.example.POJO.SetmealDish;
import com.example.Service.SetmealDishService;
import com.example.Service.SetmealService;
import com.example.common.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImp extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService{
    //新增套餐
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWidthDish(SetmealDto setmealDto) {
        //双表操作
        this.save(setmealDto);
        //setmeal操作
        //setmealdish表操作
        //获取setmealdish的数据
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);

    }

    @Override
    @Transactional
    public void removeWidthDish(List<Long> ids) {
        //查询套餐状态，没有停售不可以删除

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0){
            throw new CustomException("套餐售卖中，不能删除");
        }
        //如果可以删除，那就先删套餐，再删关系setmeal
        this.removeByIds(ids);
        //根据ids删除套餐以及套餐和菜品的关联数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
