package io.yosep.toby.user.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import io.yosep.toby.user.dao.UserDao;
import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;

public class UserServiceImpl implements UserService{
	MailSender mailSender;
	UserDao userDao;

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void add(User user) throws SQLException {
		if (user.getLevel() == null)
			user.setLevel(Level.BASIC);
		userDao.add(user);
	}

	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEMail(user);
	}

	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	
	public void upgradelevelsInternal() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}

	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
		case BASIC:
			return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER:
			return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
		case GOLD:
			return false;
		default:
			throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	private void sendUpgradeEMail(User user) {
//		Properties props = new Properties();
//		props.put("mail.smtp.host", "mail.ksug.org");
//		Session s = Session.getInstance(props,null);
//		
//		MimeMessage message = new MimeMessage(s);
//		
//		try {
//			message.setFrom(new InternetAddress("enekelx1@naver.com"));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//			message.setSubject("Upgrade 안내");
//			message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");
//			
//			Transport.send(message);
//		}catch(AddressException e) {
//			throw new RuntimeException(e);
//		}catch(MessagingException e) {
//			throw new RuntimeException(e);
//		}
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("enekelx1@naver.com");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
		
		mailSender.send(mailMessage);
	}
}
