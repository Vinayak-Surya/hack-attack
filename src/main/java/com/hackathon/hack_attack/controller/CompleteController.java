package com.hackathon.hack_attack.controller;

import com.hackathon.hack_attack.entity.AccountInfo;
import com.hackathon.hack_attack.service.CompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
@RestController
@CrossOrigin(origins = "*")
public class CompleteController {
    @Autowired
    private CompleteService completeService;

    @GetMapping("/accounts/{accountId}/balances")
    private Object fetchAccountBalances(@PathVariable String accountId) {
        return completeService.fetchAccountBalances(accountId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    private Object fetchAccountTransactions(@PathVariable String accountId) {
        return completeService.fetchAccountTransactions(accountId);
    }

//    @GetMapping("/accounts")
//    private Object fetchAccounts() {
//        return completeService.fetchAccounts();
//    }

    @GetMapping("/login")
    private List<AccountInfo> login(@RequestParam String username, @RequestParam String password) {
        return completeService.login(username, password);
    }

    @GetMapping("/accounts")
    private List<AccountInfo> allAccounts() {
        return completeService.accountInfos();
    }

    @PostMapping("/account")
    private String accountOpen() {
        return completeService.createTravelAccount();
    }


//    @GetMapping("/accounts")
//    private List<Object> login(@RequestParam String username){
//        return completeService.fetchAccountInfo(username);
//    }

    @GetMapping("/accounts/{accountId}/cards")
    private Object fetchAccountCards(@PathVariable String accountId) {
        return completeService.fetchAccountCards(accountId);
    }

    @GetMapping("/accounts/{accountId}/cards/{cardId}")
    private Object fetchAccountCardDetails(@PathVariable String accountId, @PathVariable String cards) {
        return completeService.fetchAccountCardDetails(accountId, cards);
    }

    @PostMapping("/fundTransfer")
    private String fundTransfer(@RequestParam String amount) {
        return completeService.fundTransfer(amount) == null ? "Failed" : "Successful";
    }

    @GetMapping("/accounts/{accountId}/offers")
    private Object fetchAccountOffers(@PathVariable String accountId) {
        return completeService.fetchAccountOffers(accountId);
    }

    @GetMapping("/accounts/{accountId}/statements")
    private Object fetchAccountStatements(@PathVariable String accountId) {
        return completeService.fetchAccountStatements(accountId);
    }
}
