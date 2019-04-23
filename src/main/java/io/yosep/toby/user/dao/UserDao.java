package io.yosep.toby.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.yosep.toby.user.domain.User;

public class UserDao {
	private DataSource dataSource;
	private User user;
	
	public UserDao() {}
	
	public UserDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void add(User user) throws ClassNotFoundException,SQLException{
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");

		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException{
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");

		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		rs.next();
		user = new User();
		user.setId(rs.getString(1));
		user.setName(rs.getString(2));
		user.setPassword(rs.getString(3));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
}
