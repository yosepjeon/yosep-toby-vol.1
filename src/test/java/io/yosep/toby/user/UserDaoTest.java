package io.yosep.toby.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:io/yosep/toby/user/dao/user.xml")
public class UserDaoTest {
//	@Autowired
//	private ApplicationContext context;
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/toby_spring?characterEncoding=UTF-8","enekelx1","enekeldytpq1Q@", true);
		dao.setDataSource(dataSource);
		
		this.user1 = new User("enekelx1", "enekelx1", "123123","enekelx1@gmail.com",Level.BASIC,1,0);
		this.user2 = new User("enekelx2", "enekelx2", "123123","enekelx1@gmail.com",Level.SILVER,55,10);
		this.user3 = new User("enekelx3", "enekelx3", "123123","enekelx1@gmail.com",Level.GOLD,100,40);
	}

	@Test
	public void addAndGet() throws Exception {
		this.dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		this.dao.add(user1);
		this.dao.add(user2);
		assertThat(dao.getCount(), is(2));

		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
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
	
	@Test
	public void getAll() throws SQLException{
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(),is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}
	
	@Test
	public void update()throws SQLException {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("오민규");
		user1.setPassword("111");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
}
