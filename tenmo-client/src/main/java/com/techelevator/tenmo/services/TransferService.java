package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


public class TransferService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    // method to get list of users
    public User[] getUsers() {
        User[] currentUsers = null;
        try {
            //                                       http://localhost:8080/list-users
            currentUsers = restTemplate.exchange(BASE_URL + "list-users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        } catch(RestClientException e) {
            System.out.println("Error getting list of users");
            System.out.println(e.getMessage());
        }
        return currentUsers;
    }

    // method to get the list of transfers
    public TransferUser viewTransferHistory() {
        // TODO Auto-generated method stub
        TransferUser result = null;
        try {
            //                                 http://localhost:8080/user/{id}/transfers
            result = restTemplate.exchange(BASE_URL + "user/" + currentUser.getUser().getId() + "/transfers", HttpMethod.GET, makeAuthEntity(), TransferUser.class).getBody();
        } catch(RestClientException e) {
            System.out.println("Error getting list of transfers");
            System.out.println(e.getMessage());
        }
        return result;
    }

    // method to make a transfer
    public boolean createTransfer(Transfer newTransfer) {
        boolean result = false;
        try {
            result = restTemplate.postForObject(BASE_URL + "user/" + currentUser.getUser().getId() + "/transfers",  makeTransferEntity(newTransfer), Boolean.class);
        } catch (Exception e) {
            System.out.println("Transfer cannot be completed!");
            System.out.println(e.getMessage());
        }
        return result;
    }


    // this method to put Transfer object inside the body.
    private HttpEntity<Transfer> makeTransferEntity(Transfer newTransfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
