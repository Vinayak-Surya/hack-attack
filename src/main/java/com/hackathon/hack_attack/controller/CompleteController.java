package com.hackathon.hack_attack.controller;

import com.hackathon.hack_attack.service.CompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/accounts")
    private Object fetchAccounts() {
        return completeService.fetchAccounts();
    }

    @GetMapping("/accounts/{accountId}/cards")
    private Object fetchAccountCards(@PathVariable String accountId) {
        return completeService.fetchAccountCards(accountId);
    }

    @GetMapping("/accounts/{accountId}/cards/{cardId}")
    private Object fetchAccountCardDetails(@PathVariable String accountId, @PathVariable String cards) {
        return completeService.fetchAccountCardDetails(accountId, cards);
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
