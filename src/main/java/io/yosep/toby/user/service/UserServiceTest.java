package io.yosep.toby.user.service;

import static io.yosep.toby.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static io.yosep.toby.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;
import io.yosep.toby.user.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:io/yosep/toby/user/dao/user.xml")
public class UserServiceTest {
	@Autowired
	UserServiceImpl userService;
	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	UserDao userDao;
	List<User> users;

	@Autowired
	MailSender mailSender;

	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("bumjin", "박범진", "p1", "enekelx1@gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("joytouch", "강명성", "p2", "enekelx1@gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("erwins", "신승한", "p3", "enekelx1@gmail.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
				new User("madnite1", "이상호", "p4", "enekelx1@gmail.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
				new User("green", "오민규", "p5", "enekelx1@gmail.com", Level.GOLD, 100, Integer.MAX_VALUE));
	}

	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}

	@Test
	@DirtiesContext // => 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다.
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl(); // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		
		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);

		userService.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated(); // MockUserDao로 부터 업데이트 결과를 가져온다.
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(),is(2));
		assertThat(request.get(0),is(users.get(1).getEmail()));
		assertThat(request.get(1),is(users.get(3).getEmail()));
	}

	@Test
	public void add() throws SQLException {
		userDao.deleteAll();

		User userWithLevel = users.get(4); // GOLD LEVEL
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}

	@Test
	public void upgradeAllOrNothing() throws Exception {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setMailSender(mailSender);
		
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}

		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}

		checkLevelUpgraded(users.get(1), false);
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(),is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	private void checkLevel(User user, Level expectedLevel) throws SQLException {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}

	private void checkLevelUpgraded(User user, boolean upgraded) throws SQLException {
		User userUpdate = userDao.get(user.getId());

		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}

	static class TestUserService extends UserServiceImpl {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}

	static class TestUserServiceException extends RuntimeException {
	}
	
	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage simpleMessage) throws MailException {
			// TODO Auto-generated method stub
			requests.add(simpleMessage.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage[] simpleMessages) throws MailException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users; // 레벨 업그레이드 후보 User 오브젝트 목록
		private List<User> updated = new ArrayList<User>(); // 업그레이드 대상 오브젝트를 저장해둘 목록
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		@Override
		public void add(User user) throws SQLException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public User get(String id) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		// 스텁 기능 제공
		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			return this.users;
		}

		@Override
		public void deleteAll() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() throws SQLException {
			// TODO Auto-generated method stub
			return 0;
		}

		// 목 오브젝트 기능 제공
		@Override
		public void update(User user) {
			// TODO Auto-generated method stub
			updated.add(user);
		}
		
	}
}
