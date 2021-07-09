package com.techelevator;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;


public class JdbcUserDaoTests extends DaoTests {


    private User testUser;
    private static final User USER_1 = new User(1001L, "test", "test1", "x1111x");
    private static final User USER_2 = new User(1002L, "test2", "test2", "x1111xxx");
    private JdbcUserDao sut;

    @Before
    public void setup() {
        sut = new JdbcUserDao(new JdbcTemplate(dataSource));
        testUser = new User (1000L, "testName", "testPassword", "namePassword");
    }

    @Test
    public void findAll_returns_all_users() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(USER_1);
        expectedUsers.add(USER_2);
        Assert.assertEquals(expectedUsers.size(), sut.findAll().size());
    }

    @Test
    public void findIdByUsername_returns_correct_id() {
        Assert.assertEquals(1001L, sut.findIdByUsername("test"));
    }

    @Test
    public void findByUsername_returns_correct_user() {
        Assert.assertEquals(USER_1.getId(), sut.findByUsername("test").getId());
        Assert.assertTrue(sut.findByUsername("test").getUsername().equals(USER_1.getUsername()));
    }

    @Test
    public void create_new_user() {
        boolean userCreated = sut.create(testUser.getUsername(), testUser.getPassword());
        Assert.assertTrue(userCreated);
    }


}
