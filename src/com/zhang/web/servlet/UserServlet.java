package com.zhang.web.servlet;

import com.zhang.domain.User;
import com.zhang.service.UserService;
import com.zhang.utils.CommonsUtils;
import com.zhang.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class UserServlet extends BaseServlet {

    //激活用户
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //获得激活码
        String activeCode = request.getParameter("activeCode");
        UserService service = new UserService();
        service.active(activeCode);

        //跳转到登陆
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    //检查用户是否存在
    public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");

        UserService userService = new UserService();
        boolean is = userService.checkUsername(username);
        String json = "{\"isExist\":" + is + "}";
        response.getWriter().write(json);
    }

    //注册
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        //获取表单数据
        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {
            //指定一个类型转换器将String转换为date()
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class clazz, Object value) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date par = null;
                    try {
                        par = format.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return par;
                }
            }, Date.class);
            //封装获取到的数据 beanUtils
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //补全表单
        user.setUid(CommonsUtils.getUUID());
        user.setTelephone(null);
        user.setState(0);
        String code = CommonsUtils.getUUID();
        user.setCode(code);

        //将user传递给service层
        UserService service = new UserService();
        boolean isregist = service.regist(user);

        if (isregist) {
            //发送激活邮件
            String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
                    + "<a href='http://localhost:8080/shopping/user?method=active&activeCode=" + code + "'>"
                    + "http://localhost:8080/shopping/user?method=active&activeCode=" + code + "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            //跳转成功页面
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }

    }

    //用户登录
    public void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        //获得输入的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //对密码进行加密
        //password = MD5Utils.md5(password);

        //将用户名和密码传递给service层
        UserService service = new UserService();
        User user = null;
        try {
            user = service.login(username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断用户是否登录成功 user是否是null
        if(user!=null){
            //登录成功
            //***************判断用户是否勾选了自动登录*****************
            String autoLogin = request.getParameter("autoLogin");
            if("true".equals(autoLogin)){
                //要自动登录
                //创建存储用户名的cookie
                Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
                cookie_username.setMaxAge(10*60);
                //创建存储密码的cookie
                Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
                cookie_password.setMaxAge(10*60);

                response.addCookie(cookie_username);
                response.addCookie(cookie_password);

            }

            //***************************************************
            //将user对象存到session中
            session.setAttribute("user", user);

            //重定向到首页
            response.sendRedirect(request.getContextPath()+"/index.jsp");
        }else{
            request.setAttribute("loginError", "用户名或密码错误");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    //用户注销
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession();
        //从session中将user删除
        session.removeAttribute("user");

        //将存储在客户端的cookie删除掉
        Cookie cookie_username = new Cookie("cookie_username","");
        cookie_username.setMaxAge(0);
        //创建存储密码的cookie
        Cookie cookie_password = new Cookie("cookie_password","");
        cookie_password.setMaxAge(0);

        response.addCookie(cookie_username);
        response.addCookie(cookie_password);


        response.sendRedirect(request.getContextPath()+"/login.jsp");

    }
}


