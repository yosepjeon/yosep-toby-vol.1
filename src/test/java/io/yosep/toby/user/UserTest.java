package io.yosep.toby.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:io/yosep/toby/user/dao/user.xml")
public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}

	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		
		for(Level level : levels) {
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		
		for(Level level: levels) {
			if(level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}

}
