package io.yosep.toby.user.service;

import java.sql.SQLException;
import java.util.List;

import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;

public class UserService {
	UserDao userDao;
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void add(User user)throws SQLException {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		
		for(User user : users) {
			Boolean changed = null;
			
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
				user.setLevel(Level.SILVER);
				changed = true;
			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
				user.setLevel(Level.GOLD);
				changed = true;
			}else if(user.getLevel() == Level.GOLD) {
				changed = false;
			}else {
				changed = false;
			}
			
			if(changed) {
				userDao.update(user);
			}
		}
	}
	
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
		case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER: return (user.getRecommend()>=MIN_RECOMMEND_FOR_GOLD);
		case GOLD: return false;
		default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
}
