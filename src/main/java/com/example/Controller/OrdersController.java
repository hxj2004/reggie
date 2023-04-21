package com.example.Controller;

import com.example.POJO.Orders;
import com.example.POJO.R;
import com.example.Service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;


    @PostMapping("/submit")
    private R<String> submit(@RequestBody Orders orders){
        log.info("支付");

        ordersService.submit(orders);

        return R.success("支付成功");
    }


}
