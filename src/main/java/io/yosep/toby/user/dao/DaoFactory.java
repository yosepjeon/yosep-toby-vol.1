package io.yosep.toby.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	public DaoFactory() {}
	
	@Bean
	public UserDao userDao() {
		UserDao userDao = new UserDao();
		userDao.setDataSource(dataSource());	
		return userDao;
	}
	
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/toby_spring?characterEncoding=UTF-8");
		dataSource.setUsername("enekelx1");
		dataSource.setPassword("123");
		
		return dataSource;
	}
}
