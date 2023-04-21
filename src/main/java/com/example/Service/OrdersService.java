package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.POJO.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
