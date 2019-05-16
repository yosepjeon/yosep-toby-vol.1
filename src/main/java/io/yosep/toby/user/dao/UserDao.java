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

public class UserDao {
	private DataSource dataSource;
	private User user;
	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};

	public UserDao() {
	}

	public UserDao(DataSource dataSource) {

		this.dataSource = dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * 스스로 과제: template/callback으로 바꾸기
	 */
	public void add(final User user) throws SQLException {

//      local class
//		class AddStatement implements StatementStrategy{
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				// TODO Auto-generated method stub
//				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
//				
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//			
//		}
//		
//		StatementStrategy st = new AddStatement();

//		anonymous class
//		StatementStrategy st = new StatementStrategy() {
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				// TODO Auto-generated method stub
//				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
//				
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//		};

//		=> 좀 더 간추린 방식
//		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
//
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				// TODO Auto-generated method stub
//				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
//
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//
//				return ps;
//			}
//		});

		jdbcTemplate.update("insert into users(id,name,password,level,login,recommend) values(?,?,?,?,?,?)",
				user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
				user.getRecommend());
	}

	public User get(String id) throws SQLException {
//		Connection c = dataSource.getConnection();
//		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
//
//		ps.setString(1, id);
//
//		ResultSet rs = ps.executeQuery();
//
//		if (rs.next()) {
//			user = new User();
//			user.setId(rs.getString(1));
//			user.setName(rs.getString(2));
//			user.setPassword(rs.getString(3));
//		}
//
//		rs.close();
//		ps.close();
//		c.close();
//
//		if (user == null)
//			throw new EmptyResultDataAccessException(1);
//
//		return user;

		return jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] { id }, this.userMapper);
	}

	public void deleteAll() {

//		StatementStrategy st = new DeleteAllStatement();
//		=> Anonymous방식
//		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				// TODO Auto-generated method stub
//				return c.prepareStatement("delete from users");
//			}
//		});
//		jdbcTemplate.update(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				// TODO Auto-generated method stub
//				return con.prepareStatement("delete from users");
//			}
//		});
		jdbcTemplate.update("delete from users");
	}

	public int getCount() throws SQLException {
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//
//		try {
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("select count(*) from users");
//
//			rs = ps.executeQuery();
//			rs.next();
//			int count = rs.getInt(1);
//
//			return count;
//		} catch (SQLException e) {
//			throw e;
//		} finally {
//			rs.close();
//			ps.close();
//			c.close();
//		}

//		return this.jdbcTemplate.query(new PreparedStatementCreator() {
//			
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				// TODO Auto-generated method stub
//				return con.prepareStatement("select count(*) from users");
//			}
//		}, new ResultSetExtractor<Integer>() {
//
//			@Override
//			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//				// TODO Auto-generated method stub
//				rs.next();
//				return rs.getInt(1);
//			}
//		});

		return jdbcTemplate.queryForInt("select count(*) from users");
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}

	public void update(User user) {
		this.jdbcTemplate.update("update users set name=?, password=?, level=?,login=?, " + "recommend=? where id=? ",
				user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId());
	}

	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;

		try {
			c = dataSource.getConnection();

			ps = stmt.makePreparedStatement(c);

			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}

			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {

				}
			}
		}
	}
}
