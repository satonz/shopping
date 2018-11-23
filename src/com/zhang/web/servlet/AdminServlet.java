package com.zhang.web.servlet;

import com.google.gson.Gson;
import com.zhang.domain.Category;
import com.zhang.domain.Order;
import com.zhang.domain.Product;
import com.zhang.service.AdminService;
import com.zhang.utils.BeanFactory;
import com.zhang.utils.CommonsUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminServlet extends BaseServlet {

    //增加商品
    public void adminAddProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //目的：收集表单的数据 封装一个Product实体 将上传图片存到服务器磁盘上

        Product product = new Product();

        //收集数据的容器
        Map<String,Object> map = new HashMap<String,Object>();

        try {
            //创建磁盘文件项工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //创建文件上传核心对象
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解析request获得文件项对象集合

            List<FileItem> parseRequest = upload.parseRequest(request);
            for(FileItem item : parseRequest){
                //判断是否是普通表单项
                boolean formField = item.isFormField();
                if(formField){
                    //普通表单项 获得表单的数据 封装到Product实体中
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");

                    map.put(fieldName, fieldValue);

                }else{
                    //文件上传项 获得文件名称 获得文件的内容
                    String fileName = item.getName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path+"/"+fileName);//I:/xxx/xx/xxx/xxx.jpg
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();
                    item.delete();

                    map.put("pimage", "upload/"+fileName);

                }

            }

            BeanUtils.populate(product, map);
            //是否product对象封装数据完全
            //private String pid;
            product.setPid(CommonsUtils.getUUID());
            //private Date pdate;
            product.setPdate(new Date());
            //private int pflag;
            product.setPflag(0);
            //private Category category;
            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            //将product传递给service层
            AdminService service = (AdminService) BeanFactory.getBean("adminService");
            service.saveProduct(product);





        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    //获得订单信息
    public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获得oid
        String oid = request.getParameter("oid");

        //用解耦合的方式进行编码----解web层与service层的耦合
        //使用工厂+反射+配置文件
        AdminService service = (AdminService) BeanFactory.getBean("adminService");

        List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);

        Gson gson = new Gson();
        String json = gson.toJson(mapList);
        System.out.println(json);
        /*[
         * 	{"shop_price":4499.0,"count":2,"pname":"联想（Lenovo）小新V3000经典版","pimage":"products/1/c_0034.jpg","subtotal":8998.0},
         *  {"shop_price":2599.0,"count":1,"pname":"华为 Ascend Mate7","pimage":"products/1/c_0010.jpg","subtotal":2599.0}
         *]*/
        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(json);

    }

    //获得所有的订单
    public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得所有的订单信息----List<Order>

        AdminService service = (AdminService) BeanFactory.getBean("adminService");
        List<Order> orderList = service.findAllOrders();

        request.setAttribute("orderList", orderList);

        request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);

    }

    //获得所有分类
    public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //提供一个List<Category> 转成json字符串
        AdminService service = (AdminService) BeanFactory.getBean("adminService");
        List<Category> categoryList = service.findAllCategory();

        Gson gson = new Gson();
        String json = gson.toJson(categoryList);

        response.setContentType("text/html;charset=UTF-8");

        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
