package com.example.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.OrdersMapper;
import com.example.Mapper.ShoppingCartMapper;
import com.example.POJO.*;
import com.example.Service.*;
import com.example.common.BaseContext;
import com.example.common.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImp extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    public void submit(Orders orders) {
        //获取用户id
        Long UserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,UserId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        //查看id购物车
        if(list == null || list.size() == 0){
            throw new CustomException("购物车为空");
        }



        //找用户地址
        User user = userService.getById(UserId);
        //找到订单对应的地址id
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息错误，不能下单");
        }


        AtomicInteger amount = new AtomicInteger(0);
        //订单编号
        //订单明细补全，顺便获取就、总金额
        long orderId = IdWorker.getId();
        List<OrderDetail> orderDetails = list.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount ());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;

        }).collect(Collectors.toList());

        //对于订单的值进行补全
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(UserId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName (user.getName ());
        orders.setConsignee (addressBook.getConsignee());
        orders.setPhone (addressBook.getPhone());
        orders.setAddress ((addressBook.getProvinceName() ==null ?"":addressBook.getProvinceName ()
                +(addressBook.getCityName()==null ?"":addressBook.getCityName())
                +(addressBook.getDistrictName()==null?"":addressBook.getDistrictName())
                +(addressBook.getDetail()==null?"":addressBook.getDetail())));

        this.save(orders);


        //保存订单
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
