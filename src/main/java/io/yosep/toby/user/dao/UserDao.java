package io.yosep.toby.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.yosep.toby.user.domain.Level;
import io.yosep.toby.user.domain.User;


public interface UserDao {

	void add(User user) throws SQLException;

	User get(String id) throws SQLException;

	List<User> getAll();

	void deleteAll();

	int getCount() throws SQLException;

	void update(User user);

}
