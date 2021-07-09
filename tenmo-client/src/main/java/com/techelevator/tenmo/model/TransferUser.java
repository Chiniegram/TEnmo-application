package com.techelevator.tenmo.model;

import java.util.Map;

public class TransferUser {
    private Transfer[] transfers;
    private Map<Long, String> accountsUsernames;

    public Transfer[] getTransfers() { return transfers; }

    public void setTransfers(Transfer[] transfers) { this.transfers = transfers; }

    public Map<Long, String> getAccountsUsernames() { return accountsUsernames; }

    public void setAccountsUsernames(Map<Long, String> accountsUsers) { this.accountsUsernames = accountsUsers; }
}
