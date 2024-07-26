package com.hackathon.hack_attack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.hack_attack.entity.AccountInfo;
import com.hackathon.hack_attack.entity.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CompleteService {
    private final List<LoginCredentials> loginCredentialsList;
    String token = null;

    String paymentToken = null;
    JsonNode accounts = null;
    String userId = null;
    @Autowired
    private RestTemplate restTemplate;

    public CompleteService() {
        LoginCredentials loginCredentials1 = new LoginCredentials("vinayak", "1", "123456789101");
        LoginCredentials loginCredentials2 = new LoginCredentials("demo", "demo", "123456789012");
        LoginCredentials loginCredentials3 = new LoginCredentials("ashok", "1", "123456789111");
        this.loginCredentialsList = new ArrayList<>(List.of(new LoginCredentials[]{loginCredentials1, loginCredentials2, loginCredentials3}));
    }


    // -------------------------------- ACCOUNT APIS --------------------------------

    public List<AccountInfo> accountInfos() {
        List<AccountInfo> accountInfos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            String accountNumber = accounts.get(i).get("Account").get(0).get("Identification").asText();
            String accountType = accounts.get(i).get("AccountType").asText();
            String accountId = accounts.get(i).get("AccountId").asText();
            String accountSubType = accounts.get(i).get("AccountSubType").asText();
            accountSubType = Objects.equals(accountSubType, "CurrentAccount") ? "TravelAccount" : accountSubType;
            String balance = fetchAccountBalances(accounts.get(i).get("AccountId").asText());
            accountInfos.add(new AccountInfo(accountId, accountNumber, balance, accountType, accountSubType));
        }
        return accountInfos;
    }

    public List<AccountInfo> login(String username, String password) {
        userId = loginCredentialsList.stream().filter(loginCredentials -> Objects.equals(loginCredentials.getUsername(), username)
                && Objects.equals(loginCredentials.getPassword(), password)).findFirst().map(LoginCredentials::getUserId).orElse(null);
        if (userId != null) {
            authorizationSteps(userId);
            accounts = fetchAccounts();
            return accountInfos();
        }
        return null;
    }

    public String createTravelAccount() {
        try {
            String clientId = "K84X087ivOr-uMCrAyCWZWdX5F7AiXfuwH_SPmRyAeM=", clientSecret = "korE_IPmOfR1QYAnTsWef8Efrq79LnTQt3KOSBMZ5UU=";
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("Process Start!");

            // ACCESS TOKEN
            String accessTokenBody = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=accounts", clientId, clientSecret);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<Object> entity = new HttpEntity<>(accessTokenBody, httpHeaders);
            String accessToken = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody()).get("access_token").asText();
            System.out.println(accessToken);

            // ACCOUNT ACCESS CONSENTS
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/json");
            String permission = """
                    {
                      "Data": {
                        "Permissions": [
                          "ReadAccountsBasic",
                           "NWGOpenAccount"
                        ]
                      },
                      "Risk": {}
                    }
                    """;
            entity = new HttpEntity<>(permission, httpHeaders);
            String consentId = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/account-access-consents", HttpMethod.POST, entity, String.class).getBody()).get("Data").get("ConsentId").asText();
            System.out.println(consentId);
            String message = restTemplate.getForObject(String.format("https://api.sandbox.natwest.com/authorize?client_id=%s&response_type=code id_token&scope=openid accounts&redirect_uri=%s&request=%s&authorization_mode=AUTO_POSTMAN&authorization_result=APPROVED&authorization_username=%s@www.boa-hack-attack.com&authorization_accounts=*",
                    clientId, "https://boa-hack-attack.com/login", consentId, userId), String.class);
            assert message != null;
            String authCode = message.substring(message.indexOf("=") + 1, message.indexOf("id_token"));
            System.out.println(authCode);

            String tokenBody = String.format("client_id=%s&client_secret=%s&redirect_uri=https://boa-hack-attack.com/login&grant_type=authorization_code&code=%s", clientId, clientSecret, authCode);
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            entity = new HttpEntity<>(tokenBody, httpHeaders);
            String tokenOpen = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody()).get("access_token").asText();
            System.out.println(tokenOpen);

            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + tokenOpen);
            httpHeaders.set("Content-Type", "application/json");
            String body = """
                    {
                        "name" : "My Travel Account"
                    }
                    """;
            entity = new HttpEntity<>(body, httpHeaders);
            System.out.println(entity);
            System.out.println(restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.POST, entity, String.class).getBody());
            accounts = fetchAccounts();
            return "Successful";
        } catch (Exception e) {
            return "Failed";
        }
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
                          "ReadTransactionsDetail",
                          "ReadAccountsBasic",
                          "BillPayService"
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

    public JsonNode fetchAccounts() {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.GET, entity, String.class);
            System.out.println(response);
            return new ObjectMapper().readTree(response.getBody()).get("Data").get("Account");
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    public String fundTransfer(String amount) {
        try {

            if (accounts != null) {
                System.out.println("Accounts Present");
                JsonNode jsonNode = accounts;
                String fromId = null, toId = null;
                System.out.println(jsonNode + ":" + jsonNode.size());
                for (int i = 0; i < jsonNode.size(); i++) {
                    String accountType = jsonNode.get(i).get("AccountSubType").asText();
                    if (Objects.equals(accountType, "Savings"))
                        fromId = jsonNode.get(i).get("Account").get(0).get("Identification").asText();
                    else if (Objects.equals(accountType, "CurrentAccount"))
                        toId = jsonNode.get(i).get("Account").get(0).get("Identification").asText();
                }
                if (fromId != null && toId != null) {
                    System.out.println("From - " + fromId + " To - " + toId);
                    return paymentInitAndAuth(amount, fromId, toId);
                }
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private String paymentInitAndAuth(String amount, String from, String to) {
        try {
            String clientId = "K84X087ivOr-uMCrAyCWZWdX5F7AiXfuwH_SPmRyAeM=", clientSecret = "korE_IPmOfR1QYAnTsWef8Efrq79LnTQt3KOSBMZ5UU=";
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("Process Start!");

            // ACCESS TOKEN
            String accessTokenBody = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=payments", clientId, clientSecret);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<Object> entity = new HttpEntity<>(accessTokenBody, httpHeaders);
            String accessToken = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody()).get("access_token").asText();
            System.out.println(accessToken);

            // Payment Request
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/json");
            httpHeaders.set("x-fapi-financial-id", "0015800000jfwxXAAQ");
            httpHeaders.set("x-jws-signature", "DUMMY_SIG");
            httpHeaders.set("x-idempotency-key", UUID.randomUUID().toString());
            String paymentRequest = String.format("""
                    {
                       "Data": {
                         "Initiation": {
                           "InstructionIdentification": "instr-identification",
                           "EndToEndIdentification": "e2e-identification",
                           "InstructedAmount": {
                             "Amount": "%s",
                             "Currency": "GBP"
                           },
                           "CreditorAccount": {
                             "SchemeName": "SortCodeAccountNumber",
                             "Identification": "%s",
                             "Name" : "demo"
                           }
                         }
                       },
                       "Risk": {}
                    }
                    """, amount, to);
            entity = new HttpEntity<>(paymentRequest, httpHeaders);
            String consentId = objectMapper.readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/pisp/domestic-payment-consents", HttpMethod.POST, entity, String.class).getBody()).get("Data").get("ConsentId").asText();
            System.out.println(consentId);

            // Approval
            String message = restTemplate.getForObject(String.format("https://api.sandbox.natwest.com/authorize?client_id=%s&response_type=code id_token&scope=openid payments&redirect_uri=%s&state=ABC&request=%s&authorization_mode=AUTO_POSTMAN&authorization_account=%s&authorization_username=%s@www.boa-hack-attack.com",
                    clientId, "https://boa-hack-attack.com/login", consentId, from, userId), String.class);
            System.out.println(message);
            assert message != null;
            String authCode = message.substring(message.indexOf("=") + 1, message.indexOf("id_token"));
            System.out.println(authCode);

            // TOKEN
            String tokenBody = String.format("client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code&code=%s", clientId, clientSecret, "https://boa-hack-attack.com/login", authCode);
            httpHeaders = new HttpHeaders();
//            httpHeaders.set("Authorization", "Bearer " + accessToken);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            entity = new HttpEntity<>(tokenBody, httpHeaders);
            System.out.println(entity);
            String response = restTemplate.exchange("https://ob.sandbox.natwest.com/token", HttpMethod.POST, entity, String.class).getBody();
            System.out.println(response);
            paymentToken = objectMapper.readTree(response).get("access_token").asText();
            System.out.println(paymentToken);

            // POST PAYMENT
            httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + paymentToken);
            httpHeaders.set("Content-Type", "application/json");
            httpHeaders.set("x-fapi-financial-id", "0015800000jfwxXAAQ");
            httpHeaders.set("x-jws-signature", "DUMMY_SIG");
            httpHeaders.set("x-idempotency-key", UUID.randomUUID().toString());
            String paymentPostBody = String.format("""
                    {
                      "Data": {
                        "ConsentId": "%s",
                        "Initiation": {
                          "InstructionIdentification": "instr-identification",
                          "EndToEndIdentification": "e2e-identification",
                          "InstructedAmount": {
                            "Amount": "%s",
                            "Currency": "GBP"
                          },
                          "CreditorAccount": {
                            "SchemeName": "SortCodeAccountNumber",
                            "Identification": "%s",
                            "Name" : "demo"
                          }
                        }
                      },
                      "Risk": {}
                    }
                    """, consentId, amount, to);
            entity = new HttpEntity<>(paymentPostBody, httpHeaders);
            System.out.println(entity);
            String str = new ObjectMapper().readTree(restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/pisp/domestic-payments", HttpMethod.POST, entity, String.class).getBody()).get("Data").get("DomesticPaymentId").asText();
            System.out.println(str);
            return str;
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    public String fetchAccountBalances(String accountId) {
        //
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/balances", HttpMethod.GET, entity, String.class);
            System.out.println(response.getBody());
            return new ObjectMapper().readTree(Objects.requireNonNull(response.getBody())).get("Data").get("Balance").get(0).get("Amount").get("Amount").asText();
        } catch (Exception e) {
            return "0.00";
        }
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