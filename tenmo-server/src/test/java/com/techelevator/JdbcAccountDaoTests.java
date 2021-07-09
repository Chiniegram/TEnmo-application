package com.techelevator;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;


public class JdbcAccountDaoTests extends DaoTests {

    private static final Account ACCOUNT_1 = new Account(2001L, 1001L, new BigDecimal(1000.00));
    private JdbcAccountDao sut;
    private UserDao userDao = new JdbcUserDao(new JdbcTemplate(dataSource));

    @Before
    public void setup() {
        sut = new JdbcAccountDao(dataSource, userDao);
    }

    @Test
    public void getBalance_returns_correct_balance() {
        BigDecimal actualBalance = sut.getBalance("test");
        assertBalanceMatch(ACCOUNT_1.getBalance(), actualBalance);

    }

    @Test
    public void findUsername_finds_correct_username() {
        Assert.assertTrue(sut.findUsernameByAccountId(2001L).equals("test"));
    }

    @Test
    public void findAccountIdByUsername_finds_correct_accountId() {
        Assert.assertTrue(sut.findAccountIdByUsername("test").equals(2001L));
    }

    @Test
    public void findAccountIdByUserId_finds_correct_accountId() {
        Assert.assertTrue(sut.findAccountIdByUserId(1001L).equals(2001L));
    }

    private void assertBalanceMatch(BigDecimal expected, BigDecimal actual) {
        double expectedBalance = expected.doubleValue();
        double actualBalance = actual.doubleValue();
        Assert.assertEquals(expectedBalance, actualBalance, 0.001);
    }

}
