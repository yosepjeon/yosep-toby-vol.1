package io.yosep.toby.user.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import io.yosep.toby.user.domain.User;

public class UserServiceTx implements UserService {
	UserService userService; // 타깃 오브젝트
	PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	// 메소드 구현과 위임
	@Override
	public void add(User user) throws SQLException {
		// TODO Auto-generated method stub
		userService.add(user);
	}

	// 메소드 구현
	@Override
	public void upgradeLevels() {
		// TODO Auto-generated method stub
		// 부가기능 수행
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			userService.upgradeLevels(); // 위임

			this.transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

}
