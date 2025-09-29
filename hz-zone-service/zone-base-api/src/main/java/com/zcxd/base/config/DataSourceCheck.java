package com.zcxd.base.config; // 改成你自己的包名

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DataSourceCheck {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void checkDB() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 打印当前数据库和用户
            ResultSet rs = stmt.executeQuery("SELECT DATABASE() AS db_name, USER() AS user_name");
            if (rs.next()) {
                System.out.println("Program connected DB: " + rs.getString("db_name"));
                System.out.println("Program connected USER: " + rs.getString("user_name"));
            }

            // 打印 JDBC URL（HikariDataSource 的 URL）
            System.out.println("JDBC URL: " + ((com.zaxxer.hikari.HikariDataSource)dataSource).getJdbcUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
