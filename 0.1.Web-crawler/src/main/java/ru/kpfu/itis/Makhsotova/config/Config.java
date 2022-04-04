package ru.kpfu.itis.Makhsotova.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import ru.kpfu.itis.Makhsotova.repository.*;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Data
public class Config {

    private DataSource dataSource;
    private LinkRepository linkRepository;
    public Config() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/Users/milanamahsotova/Desktop/HWInfoProjects/DataMining/src/main/resources/db.properties"));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty("db.url"));
        hikariConfig.setPassword(properties.getProperty("db.password"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.hikari.pool-size")));
        hikariConfig.setUsername(properties.getProperty("db.user"));
        hikariConfig.setDriverClassName(properties.getProperty("db.driver"));

        dataSource = new HikariDataSource(hikariConfig);

        initRepository();
    }

    protected void initRepository() {
        linkRepository = new LinkRepositoryJdbcImpl(dataSource);
    }
}
