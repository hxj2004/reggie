package com.example.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.POJO.R;
import com.example.POJO.User;
import com.example.Service.UserService;
import com.example.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    public R<String> senMsg(@RequestBody User user, HttpSession session){

        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //这里把发送的短信保存在redis中，并设置时间为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            //session.setAttribute(phone,code);
            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取电话号码
        log.info("登录{}",map.get("phone"));
        String phone = map.get("phone").toString();
        //校验验证码是否正确，从session中取出验证码，在对于redis中保存的value值对比，查看是否正确
        String code = map.get("code").toString();
        //获取验证码
        //Object codeInSession = session.getAttribute(phone);

        //标注为///的为缓存数据
        //从redis中获取对应的验证码做对比
        ///Object codeInSession =redisTemplate.opsForValue().get(phone);

        //对比验证码是否正确
        ///if(codeInSession != null && codeInSession.equals(code)){

        if(phone != null){

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果登录成功，删除redis中保存的验证码
            ///redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
