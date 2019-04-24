package io.yosep.toby.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:io/yosep/toby/user/dao/user.xml")
public class UserDaoTest {
	@Autowired
	private ApplicationContext context;
	
	private UserDao dao;
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		System.out.println(this.context);
		System.out.println(this);
		
		this.dao = this.context.getBean("userDao", UserDao.class);
		
		this.user1 = new User("enekelx1", "enekelx1", "123123");
		this.user2 = new User("enekelx2", "enekelx2", "123123");
		this.user3 = new User("enekelx3", "enekelx3", "123123");
	}

	@Test
	public void addAndGet() throws Exception {
		this.dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		this.dao.add(user1);
		this.dao.add(user2);
		assertThat(dao.getCount(), is(2));

		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));

		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
	}

	@Test
	public void count() throws SQLException {
		this.dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		this.dao.add(user1);
		assertThat(dao.getCount(), is(1));

		this.dao.add(user2);
		assertThat(dao.getCount(), is(2));

		this.dao.add(user3);
		assertThat(dao.getCount(), is(3));

	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.get("enekelx1");
	}
}
