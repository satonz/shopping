package com.zhang.service;

import com.zhang.dao.UserDao;
import com.zhang.domain.User;

import java.sql.SQLException;

public class UserService {

    public boolean regist(User user) {
        UserDao dao = new UserDao();
        int row = dao.regist(user);
        return row>0?true:false;
    }

    public void active(String activeCode) {
        UserDao dao = new UserDao();
        try {
            dao.active(activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsername(String username) {
        UserDao dao = new UserDao();
        long l = dao.checkUsername(username);
        return l>0?true:false;
    }

    //用户登录的方法
    public User login(String username, String password) throws SQLException {
        UserDao dao = new UserDao();
        return dao.login(username,password);
    }

}
