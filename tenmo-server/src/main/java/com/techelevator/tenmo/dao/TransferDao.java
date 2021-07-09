package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import java.util.List;

public interface TransferDao {

    List<Transfer> findAll(String username);
    boolean create(Transfer t);

}
