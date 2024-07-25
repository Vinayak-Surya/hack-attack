package com.hackathon.hack_attack.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.hack_attack.entity.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CompleteService {
    private final List<LoginCredentials> loginCredentialsList;
    String token = null;
    @Autowired
    private RestTemplate restTemplate;

    public CompleteService() {
        LoginCredentials loginCredentials1 = new LoginCredentials("abc1", "abc6", "123456789101");
        LoginCredentials loginCredentials2 = new LoginCredentials("abc2", "abc5", "123456789012");
        this.loginCredentialsList = new ArrayList<>(List.of(new LoginCredentials[]{loginCredentials1, loginCredentials2}));
    }


    // -------------------------------- ACCOUNT APIS --------------------------------

    public Object login(String username, String password) {
        String userId = loginCredentialsList.stream().filter(loginCredentials -> Objects.equals(loginCredentials.getUsername(), username)
                && Objects.equals(loginCredentials.getPassword(), password)).findFirst().map(LoginCredentials::getUserId).orElse(null);
        if(userId != null){
            authorizationSteps(userId);
            return fetchAccounts();
        }
        return null;
    }

    private void authorizationSteps(String userId) {
        try {
            String clientId = "K84X087ivOr-uMCrAyCWZWdX5F7AiXfuwH_SPmRyAeM=", clientSecret = "korE_IPmOfR1QYAnTsWef8Efrq79LnTQt3KOSBMZ5UU=";
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("Process Start!");

            // ACCESS TOKEN
            String accessTokenBody = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=accounts", clientId, clientSecret);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            String permission = """
                    {
                      "Data": {
                        "Permissions": [
                          "ReadAccountsDetail",
                          "ReadBalances",
                          "ReadTransactionsCredits",
                          "ReadTransactionsDebits",
                          "ReadTransactionsDetail"
                        ]
                      },
                      "Risk": {}
                    }
                    """;
            HttpEntity<Object> entity = new HttpEntity<>(accessTokenBody, httpHeaders);
            String accessToken = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody()).get("access_token").asText();
            System.out.println(accessToken);

            // ACCOUNT ACCESS CONSENTS
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/json");
            entity = new HttpEntity<>(permission, httpHeaders);
            String consentId = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/account-access-consents", HttpMethod.POST, entity, String.class).getBody()).get("Data").get("ConsentId").asText();
            System.out.println(consentId);

            // AUTH TOKEN
            String message = restTemplate.getForObject(String.format("https://api.sandbox.natwest.com/authorize?client_id=%s&response_type=code id_token&scope=openid accounts&redirect_uri=%s&request=%s&authorization_mode=AUTO_POSTMAN&authorization_result=APPROVED&authorization_username=%s@www.boa-hack-attack.com&authorization_accounts=*",
                    clientId, "https://boa-hack-attack.com/login", consentId, userId), String.class);
            assert message != null;
            String authCode = message.substring(message.indexOf("=") + 1, message.indexOf("id_token"));
            System.out.println(authCode);

            // TOKEN
            String tokenBody = String.format("client_id=%s&client_secret=%s&redirect_uri=https://boa-hack-attack.com/login&grant_type=authorization_code&code=%s", clientId, clientSecret, authCode);
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            entity = new HttpEntity<>(tokenBody, httpHeaders);
            token = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody()).get("access_token").asText();
            System.out.println(token);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public Object fetchAccounts() {
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    private Object fetchAccountDetails(String accountId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId, HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountBalances(String accountId) {
        //
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/balances", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountTransactions(String accountId) {
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/transactions", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    // -------------------------------------- CARD APIS -------------------------------------
    public Object fetchAccountCards(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/cards", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountCardDetails(String accountId, String cardId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/cards/" + cardId, HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountOffers(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/offers", HttpMethod.GET, entity, Object.class);
            System.out.println(response);
            return response.getBody();
        } catch (Exception e) {
            return new ResponseEntity<>("Offers couldn't be fetched. Offer information only available for Credit Card accounts", HttpStatus.BAD_REQUEST);
        }
    }

    public Object fetchAccountStatements(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/statements", HttpMethod.GET, entity, Object.class);
            System.out.println(response);
            return response.getBody();
        } catch (Exception e) {
            return new ResponseEntity<>("Statement couldn't be fetched. Statement information only available for Credit Card accounts", HttpStatus.BAD_REQUEST);
        }
    }
}