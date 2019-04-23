package io.yosep.toby.user;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import io.yosep.toby.user.dao.DaoFactory;
import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.User;

public class UserDaoTest {
	public static void main(String[] args ) throws ClassNotFoundException, SQLException{
		ApplicationContext context = new GenericXmlApplicationContext("io/yosep/toby/user/dao/user.xml");
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("userDao",UserDao.class);
		User user = new User();
		user.setId("enekelx1");
		user.setName("enekelx1");
		user.setPassword("123123");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user.getId() + " 조회 성공");
	}
}
