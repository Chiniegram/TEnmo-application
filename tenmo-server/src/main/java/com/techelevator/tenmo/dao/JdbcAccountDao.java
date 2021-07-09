package com.techelevator.tenmo.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;


    public JdbcAccountDao(DataSource dataSource, UserDao userDao) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDao = userDao;
    }

    @Override
    public BigDecimal getBalance(String username) {
        int userId = userDao.findIdByUsername(username);
        BigDecimal balance = null;
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                balance = results.getBigDecimal("balance");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return balance;
    }

    @Override
    public String findUsernameByAccountId(Long accountId) {
        String username = "";
        String sql = "SELECT username FROM users " +
                "JOIN accounts ON users.user_id = accounts.user_id " +
                "WHERE accounts.account_id = ?";
        SqlRowSet results;
        try {
            results = jdbcTemplate.queryForRowSet(sql, accountId);
            if (results.next()) {
                username = results.getString("username");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return username;
    }

    @Override
    public Long findAccountIdByUsername(String username) {
        Long accountId = null;
        String sql = "SELECT account_id FROM accounts " +
                "JOIN users ON users.user_id = accounts.user_id " +
                "WHERE username = ?";
        SqlRowSet results;
        try {
            results = jdbcTemplate.queryForRowSet(sql, username);
            if (results.next()) {
                accountId = results.getLong("account_id");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return accountId;
    }

    @Override
    public Long findAccountIdByUserId(Long userId) {
        Long accountId = null;
        String sql = "SELECT account_id FROM accounts " +
                "JOIN users ON users.user_id = accounts.user_id " +
                "WHERE users.user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                accountId = results.getLong("account_id");
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return accountId;
    }

}
