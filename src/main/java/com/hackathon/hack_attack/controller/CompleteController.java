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
    private Object fetchAccountBalances(@RequestParam String accountId) {
        return completeService.fetchAccountBalances(accountId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    private Object fetchAccountTransactions(@RequestParam String accountId) {
        return completeService.fetchAccountTransactions(accountId);
    }

    @GetMapping("/accounts")
    private Object fetchAccounts() {
        return completeService.fetchAccounts();
    }
}
