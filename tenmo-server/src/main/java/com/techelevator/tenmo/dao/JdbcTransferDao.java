package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    // objects to have access to the database
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private UserDao userDao;

    public JdbcTransferDao (DataSource datasource, AccountDao accountDao, UserDao userDao) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
        this.accountDao = accountDao;
        this.userDao = userDao;}



    @Override
    // since we want to be sure that we providing the information only linked with our current user
    // we make search based on principal's name
    public List<Transfer> findAll(String username) {
        // here we look for principal's id in the database
        int userId = userDao.findIdByUsername(username);
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, amount, account_from, account_to, transfer_status_id, transfer_type_id " +
                "FROM transfers WHERE account_from = (SELECT account_id FROM accounts JOIN users ON accounts.user_id = users.user_id WHERE users.user_id = ?) " +
                "OR account_to = (SELECT account_id FROM accounts JOIN users ON accounts.user_id = users.user_id WHERE users.user_id = ?);";
        // making a call to database for user's transfers that linked to his id
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        // put the table info into the list line by line
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }


    @Override
    public boolean create(Transfer t) {
        // x is going to be our response
        boolean x = false;
        // checking if user have enough money on the balance to make a transfer
        int result = accountDao.getBalance(accountDao.findUsernameByAccountId(t.getAccountFrom())).compareTo(t.getAmount());
        if (result > 0) {
            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
            Integer newTransferId;
            try {
                newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, t.getTypeId(), t.getStatusId(), t.getAccountFrom(), t.getAccountTo(), t.getAmount());
                // if yes - x becomes true
                x = true;
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
                return x;
            }

            String sql1 = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?;";
            try {
                jdbcTemplate.update(sql1, t.getAmount(), t.getAccountTo());
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
                return x;
            }

            String sql2 = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?;";
            try {
                jdbcTemplate.update(sql2, t.getAmount(), t.getAccountFrom());
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
                return x;
            }
            return x;
        } // if not enough money x returns with its default value - false
        return x;
    }

    private Transfer mapRowToTransfer(SqlRowSet trans) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(trans.getLong("transfer_id"));
        transfer.setStatusId(trans.getLong("transfer_status_id"));
        transfer.setTypeId(trans.getLong("transfer_type_id"));
        transfer.setAccountFrom(trans.getLong("account_from"));
        transfer.setAccountTo(trans.getLong("account_to"));
        transfer.setAmount(trans.getBigDecimal("amount"));
        return transfer;
    }



}
