package com.hackathon.hack_attack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.hack_attack.entity.AccountInfo;
import com.hackathon.hack_attack.entity.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CompleteService {
    private final List<LoginCredentials> loginCredentialsList;
    String token = null;
    String paymentToken = null;
    JsonNode accounts = null;
    String userId = null;

    Map<String, String> insuranceMap = new HashMap<>();
    @Autowired
    private RestTemplate restTemplate;

    public CompleteService() {
        LoginCredentials loginCredentials1 = new LoginCredentials("vinayak", "1", "123456789101");
        LoginCredentials loginCredentials2 = new LoginCredentials("demo", "demo", "123456789012");
        LoginCredentials loginCredentials3 = new LoginCredentials("ashok", "1", "123456789111");
        this.loginCredentialsList = new ArrayList<>(List.of(new LoginCredentials[]{loginCredentials1, loginCredentials2, loginCredentials3}));
    }

    public String login(String username, String password) {
        userId = loginCredentialsList.stream().filter(loginCredentials -> Objects.equals(loginCredentials.getUsername(), username)
                && Objects.equals(loginCredentials.getPassword(), password)).findFirst().map(LoginCredentials::getUserId).orElse(null);
        if (userId != null) return "Successful";
        return "Failed";
    }

    public String travelInsuranceAccountRouter(String amount, String period) {
        try {
            String travelAccountStatus = createTravelAccount();
            String fundTransferStatus = !Objects.equals(amount, "0") ? fundTransfer(amount, 0) : "";
            String insuranceAmount = null;
            if (Objects.equals(period, "3")) insuranceAmount = "15";
            else if (Objects.equals(period, "6")) insuranceAmount = "25";
            else if (Objects.equals(period, "12")) insuranceAmount = "40";
            String insuranceStatus = null;
            if (insuranceAmount != null) {
                insuranceStatus = fundTransfer(insuranceAmount, Integer.parseInt(period));
            }
            System.out.println(travelAccountStatus);
            System.out.println(fundTransferStatus);
            System.out.println(insuranceStatus);
            return Objects.equals(travelAccountStatus, "Successful") && fundTransferStatus != null && insuranceStatus != null ? "Successful" : "Failed";
        } catch (Exception e) {
            return "Failed";
        }
    }

    public List<AccountInfo> accountInfos() {
        accounts = fetchAccounts();
        System.out.println(accounts);
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
            authorizationSteps(userId);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.GET, entity, String.class);
            return new ObjectMapper().readTree(response.getBody()).get("Data").get("Account");
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    public String fundTransfer(String amount, Integer period) {
        try {
            accounts = fetchAccounts();
            if (accounts != null) {
                System.out.println(amount);
                System.out.println(period);
                System.out.println("Accounts Present");
                JsonNode jsonNode = accounts;
                String fromId = null, toId = null;
                System.out.println(jsonNode + ":" + jsonNode.size());
                for (int i = 0; i < jsonNode.size(); i++) {
                    String accountType = jsonNode.get(i).get("AccountSubType").asText();
                    if (Objects.equals(accountType, "Savings"))
                        fromId = jsonNode.get(i).get("Account").get(0).get("Identification").asText();
                    else if (period == 0 && Objects.equals(accountType, "CurrentAccount"))
                        toId = jsonNode.get(i).get("Account").get(0).get("Identification").asText();
                }
                toId = period != 0 ? "51234512345660" : toId;
                if (fromId != null && toId != null) {
                    System.out.println("From - " + fromId + " To - " + toId);
                    String paymentStatus = paymentInitAndAuth(amount, fromId, toId);

                    if (period != 0 && paymentStatus != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.MONTH, period);
                        insuranceMap.put(userId, new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
                    }
                }
                return "Success";
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
            String paymentTo = Objects.equals(to, "51234512345660") ? "Travel Insurance" : "Travel Account";

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
                           },
                           "RemittanceInformation": {
                                   "Unstructured": "%s",
                                   "Reference": "%s"
                           }
                         }
                       },
                       "Risk": {}
                    }
                    """, amount, to, paymentTo, paymentTo);
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
                          },
                           "RemittanceInformation": {
                                   "Unstructured": "%s",
                                   "Reference": "%s"
                           }
                        }
                      },
                      "Risk": {}
                    }
                    """, consentId, amount, to, paymentTo, paymentTo);
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

    public JsonNode fetchAccountTransactions(String accountId) {
        try {
            if (userId != null) {
                authorizationSteps(userId);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Authorization", "Bearer " + token);
                HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
                ResponseEntity<String> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/transactions", HttpMethod.GET, entity, String.class);
                JsonNode transactions = new ObjectMapper().readTree(response.getBody()).get("Data").get("Transaction");
                System.out.println(transactions);
                return transactions;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String isInsured() {
        return userId != null ? insuranceMap.getOrDefault(userId, "") : "";
    }
}