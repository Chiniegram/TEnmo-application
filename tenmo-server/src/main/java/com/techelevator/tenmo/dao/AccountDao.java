package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalance(String username);
    String findUsernameByAccountId(Long accountId);
    Long findAccountIdByUsername(String username);
    Long findAccountIdByUserId(Long userId);
}
