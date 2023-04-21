package com.example.common;
//基于ThreadLocal封装工具类，用户保存和获取当前登录的id
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
//    在线程请求上写入id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
//    获取线程上的id
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
