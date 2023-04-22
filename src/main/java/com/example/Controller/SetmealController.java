package com.example.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.DTO.SetmealDto;
import com.example.POJO.Category;
import com.example.POJO.R;
import com.example.POJO.Setmeal;
import com.example.POJO.SetmealDish;
import com.example.Service.CategoryService;
import com.example.Service.SetmealDishService;
import com.example.Service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("setmeal")
@RestController
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    @CacheEvict(value = "setmealCeche", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("添加套餐{}",setmealDto);
        //多表操作
        setmealService.saveWidthDish(setmealDto);

        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        log.info("套餐分页查询{},{},{}",page, pageSize, name);
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);

        //分页数据
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> list = setmealList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //获取到id
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);
            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            //根据id获取到名字
            return setmealDto;
            //名字赋值
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
//    删除套餐
    @DeleteMapping
    @CacheEvict(value = "setmealCeche", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWidthDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    //缓存注解,缓存分类未setmealCache，key为"#setmeal.categoryId + '_' + #setmeal.status"就是分类id和是否停售
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("查询套餐数据",setmeal);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);

    }

}
