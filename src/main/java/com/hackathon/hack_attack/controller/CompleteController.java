package com.hackathon.hack_attack.controller;

import com.fasterxml.jackson.databind.JsonNode;
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

    @GetMapping("/login")
    private String login(@RequestParam String username, @RequestParam String password) {
        return completeService.login(username, password);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    private JsonNode fetchAccountTransactions(@PathVariable String accountId) {
        return completeService.fetchAccountTransactions(accountId);
    }

    @GetMapping("/accounts")
    private List<AccountInfo> allAccounts() {
        return completeService.accountInfos();
    }

    @PostMapping("/applyCreditCard")
    private String applyCreditCard() {
        return completeService.createCreditCard();
    }

    @PostMapping("/payment/{card}")
    private String payment(@RequestParam String amount, @PathVariable String card) {
        return completeService.fundTransfer(amount, card);
    }
}
