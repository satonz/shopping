package com.zhang.dao;

import com.zhang.domain.Category;
import com.zhang.domain.Order;
import com.zhang.domain.OrderItem;
import com.zhang.domain.Product;
import com.zhang.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductDao {
    //查询热门商品
    public List<Product> findHotProduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot = ? limit ?,?";
        return runner.query(sql, new BeanListHandler<Product>(Product.class), 1, 0, 9);
    }

    //查询最新商品
    public List<Product> findnewProduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product order by pdate desc limit ?,?";
        return runner.query(sql, new BeanListHandler<Product>(Product.class),  0, 9);
    }

    //查询分类
    public List<Category> findallCategory() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select * from category";
       return runner.query(sql, new BeanListHandler<Category>(Category.class));
    }

    //根据分类查询总条数
    public int getCount(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid=?";
        Long query = (Long) runner.query(sql, new ScalarHandler(),cid);
        return query.intValue();
    }

    //查询当前页商品
    public List<Product> findProductByPage(String cid, int index, int currentCount) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid=? limit ?,?";
        List<Product> list = runner.query(sql, new BeanListHandler<Product>(Product.class), cid,index,currentCount);
        return list;
    }

    public Product findProductByPid(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where pid=?";
        return runner.query(sql, new BeanHandler<Product>(Product.class), pid);
    }


    //向orders表插入数据
    public void addOrders(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        runner.update(conn,sql, order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),
                order.getAddress(),order.getName(),order.getTelephone(),order.getUser().getUid());
    }

    //向orderitem表插入数据
    public void addOrderItem(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orderitem values(?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem item : orderItems){
            runner.update(conn,sql,item.getItemid(),item.getCount(),item.getSubtotal(),item.getProduct().getPid(),item.getOrder().getOid());
        }


    }

    public void updateOrderAdrr(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set address=?,name=?,telephone=? where oid=?";
        runner.update(sql, order.getAddress(),order.getName(),order.getTelephone(),order.getOid());
    }

    public void updateOrderState(String r6_Order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set state=? where oid=?";
        runner.update(sql, 1,r6_Order);
    }


    public List<Order> findAllOrders(String uid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders where uid=?";
        return runner.query(sql, new BeanListHandler<Order>(Order.class), uid);
    }

    public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select i.count,i.subtotal,p.pimage,p.pname,p.shop_price from orderitem i,product p where i.pid=p.pid and i.oid=?";
        List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler(), oid);
        return mapList;
    }
}
