package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public AccountService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    // method to get the balance
    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);

        try {
            balance = restTemplate.exchange(BASE_URL + "balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Your current balance is: $" + balance);
        } catch (RestClientException e) {
            System.out.println("Error getting balance");
            System.out.println(e.getMessage());
        }
        return balance;
    }


    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
