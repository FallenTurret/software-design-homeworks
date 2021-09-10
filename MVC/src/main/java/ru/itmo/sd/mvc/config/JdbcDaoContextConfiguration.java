package ru.itmo.sd.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.itmo.sd.mvc.dao.ThingsToDoDao;
import ru.itmo.sd.mvc.dao.ThingsToDoJdbcDao;

import javax.sql.DataSource;

@Configuration
public class JdbcDaoContextConfiguration {
    @Bean
    public ThingsToDoDao thingsToDoDao(DataSource dataSource) {
        return new ThingsToDoJdbcDao(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:things.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
