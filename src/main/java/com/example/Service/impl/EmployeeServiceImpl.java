package com.example.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.EmployeeMapper;
import com.example.POJO.Employee;
import com.example.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{

//    @Override
//    public Employee getOne(Employee employee) {
//        return employeeMapper.getOne(employee);
//    }
}
