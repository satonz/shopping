package com.zhang.dao;

import com.zhang.domain.User;
import com.zhang.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UserDao {
    public int regist(User user) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";
        int update = 0;
        try {
            update = runner.update(sql, user.getUid(),user.getUsername(),user.getPassword(),
                    user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),
                    user.getSex(),user.getState(),user.getCode());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return update;

    }

    public void active(String activeCode) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update user set state=? where code=?";
        runner.update(sql, 1,activeCode);
    }

    public long checkUsername(String username) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from user where username = ?";
        long query = 0;
        try {
           query = (long) runner.query(sql, new ScalarHandler(), username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return query;
    }

    //用户登录的方法
    public User login(String username, String password) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from user where username=? and password=?";
        return runner.query(sql, new BeanHandler<User>(User.class), username,password);
    }

}
