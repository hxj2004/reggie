package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.POJO.Category;
import com.example.POJO.Employee;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
