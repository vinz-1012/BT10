package vn.iotstar.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String url = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        if (url == null || !url.contains("jdbc:sqlserver:")) {
            return;
        }

        try {
            // Trích xuất tên database từ URL (ví dụ: databaseName=jwt_demo hoặc database=jwt_demo)
            Pattern pattern = Pattern.compile("(?i)(databaseName|database)=([^;]+)");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                String dbName = matcher.group(2).trim();

                // Thay thế databaseName thành 'master' để kết nối tạo CSDL
                String masterUrl = url.replaceAll("(?i)(databaseName|database)=([^;]+)", "$1=master");

                log.info("Dang kiem tra su ton tai cua database [{}] tren SQL Server...", dbName);

                try (Connection conn = DriverManager.getConnection(masterUrl, username, password);
                     Statement stmt = conn.createStatement()) {

                    String sql = String.format(
                            "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'%s') " +
                            "BEGIN " +
                            "    CREATE DATABASE [%s]; " +
                            "END",
                            dbName, dbName
                    );

                    stmt.executeUpdate(sql);
                    log.info("Database [{}] da duoc tao hoac da ton tai san sang.", dbName);
                }
            }
        } catch (Exception e) {
            log.warn("Luu y kiem tra tao database tu dong: {}. Neu CSDL da tao san, chuong trinh se tiep tuc...", e.getMessage());
        }
    }
}
