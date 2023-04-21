package com.example;

import com.example.Mapper.EmployeeMapper;
import com.example.POJO.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    private EmployeeMapper employeeMapper;
    @Test
    void select(){
        List<Employee> list = employeeMapper.selectList(null);
        list.forEach(System.out::println);
    }

}
