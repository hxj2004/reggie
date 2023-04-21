package com.example.filter;

import com.alibaba.fastjson.JSON;
import com.example.common.BaseContext;
import com.example.POJO.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest Request, ServletResponse Response, FilterChain Chain) throws IOException, ServletException {
        //转为http的对象
        HttpServletRequest httpRequest = (HttpServletRequest) Request;
        HttpServletResponse httpResponse = (HttpServletResponse) Response;
        log.info("拦截到请求{}",httpRequest.getRequestURL());

        //获取到请求路径
        String requestURL = httpRequest.getRequestURI();
        //哪些路径不会被拦截
        String[] urls = new String[]{
                "/employee/login"
                ,"/employee/logout"
                , "/backend/**"
                , "/front/**"
                ,"/common/**"
                ,"/user/sendMsg"
                ,"/user/login"
        };
        //如果路径为不被拦截的路径就放行
        boolean check = check(requestURL,urls);
        if(check){
            log.info("访问为不需要权限的页面");
            Chain.doFilter(httpRequest,httpResponse);
            return;
        }

        //判断路径中是否有Session，对应的数据是否不为空，不为空就放行
        if(httpRequest.getSession().getAttribute("employee") != null){
            //            传递登录着的id写在线程上
            Long Empid = (Long) httpRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(Empid);

            Chain.doFilter(httpRequest,httpResponse);
            return;
        }

        //手机端判断
        //判断路径中是否有Session，对应的数据是否不为空，不为空就放行
        if(httpRequest.getSession().getAttribute("user") != null){
            //            传递登录着的id写在线程上
            Long userId = (Long) httpRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            Chain.doFilter(httpRequest,httpResponse);
            return;
        }
        //把报错信息放回给前端，通过输出流来写，返回给前端
        httpResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("请求失败，未登录");
        return;

    }


    private boolean check(String requestURL, String[] urls) {
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestURL)){
                return true;
            }
        }
        return false;
    }
}