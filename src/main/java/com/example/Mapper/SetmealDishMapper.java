package com.example.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.POJO.SetmealDish;
import com.example.Service.SetmealDishService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.PriorityBlockingQueue;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

}
