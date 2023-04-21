package com.example.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.POJO.Category;
import com.example.POJO.Employee;
import com.example.POJO.R;
import com.example.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }
    @GetMapping("/page")
    //page页数pagesize每页数据name查询的名字
    public R<Page> page(Integer page, Integer pageSize){
        log.info("page{} pagesize{}",page,pageSize);
        //分页器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        ////条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加
        queryWrapper.orderByAsc(Category::getSort);

//        排序条件
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类id{}",ids);
        //categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");

    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> listR(Category category){
        //添加条件器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());

        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

         List<Category> list = categoryService.list(lambdaQueryWrapper);

        //返回
        return R.success(list);

    }
}
