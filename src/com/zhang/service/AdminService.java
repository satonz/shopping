package com.zhang.service;

import com.zhang.domain.Category;
import com.zhang.domain.Order;
import com.zhang.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AdminService {

        public List<Category> findAllCategory();

        public void saveProduct(Product product) throws SQLException;

        public List<Order> findAllOrders();

        public List<Map<String, Object>> findOrderInfoByOid(String oid);
    }

