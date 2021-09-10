package ru.itmo.sd.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.sd.mvc.dao.ThingsToDoDao;
import ru.itmo.sd.mvc.dao.ThingsToDoInMemoryDao;

//@Configuration
public class InMemoryDaoContextConfiguration {
    @Bean
    public ThingsToDoDao thingsToDoDao() {
        return new ThingsToDoInMemoryDao();
    }
}
