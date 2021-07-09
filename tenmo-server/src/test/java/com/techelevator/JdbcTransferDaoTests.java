package com.techelevator;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDaoTests extends DaoTests {

    private JdbcTransferDao sut;
    private AccountDao accountDao;
    private UserDao userDao;
    private TransferDao transferDao;
    private static final Transfer TRANSFER_1 = new Transfer(2001L, 2002L, new BigDecimal(50), 2L, 2L);
    private static final Transfer TRANSFER_2 = new Transfer(2002L, 2001L, new BigDecimal(200), 2L, 2L);
    private Transfer testTransfer;

    @Before
    public void setup() {
        userDao = new JdbcUserDao(new JdbcTemplate(dataSource));
        accountDao = new JdbcAccountDao(dataSource, userDao);
        sut = new JdbcTransferDao(dataSource, accountDao, userDao);
        testTransfer = new Transfer(2001L, 2002L, new BigDecimal(111), 2L, 2L);
    }

    @Test
    public void findAll_returns_all_transfer() {
        List<Transfer> expectedTransfers = new ArrayList<>();
        expectedTransfers.add(TRANSFER_1);
        expectedTransfers.add(TRANSFER_2);
        Assert.assertEquals(expectedTransfers.size(), sut.findAll("test").size());
    }

    @Test
    public void create_new_transfer() {
        boolean transferCreated = sut.create(testTransfer);
        Assert.assertTrue(transferCreated);
    }


}