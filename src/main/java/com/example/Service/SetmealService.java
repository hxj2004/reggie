package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.DTO.SetmealDto;
import com.example.POJO.Setmeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    public void saveWidthDish(SetmealDto setmealDto);

    void removeWidthDish(List<Long> ids);
}
