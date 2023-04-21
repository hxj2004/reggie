package com.example.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Mapper.AddressBookMapper;
import com.example.POJO.AddressBook;
import com.example.Service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImp extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {
}
