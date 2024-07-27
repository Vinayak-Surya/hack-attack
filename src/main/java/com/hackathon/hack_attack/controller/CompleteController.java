package com.hackathon.hack_attack.controller;

import com.hackathon.hack_attack.entity.AccountInfo;
import com.hackathon.hack_attack.service.CompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequestMapping
@RestController
@CrossOrigin(origins = "*")
public class CompleteController {
    @Autowired
    private CompleteService completeService;

//    @GetMapping("/accounts/{accountId}/balances")
//    private Object fetchAccountBalances(@PathVariable String accountId) {
//        return completeService.fetchAccountBalances(accountId);
//    }

    @GetMapping("/accounts/{accountId}/transactions")
    private Object fetchAccountTransactions(@PathVariable String accountId) {
        return completeService.fetchAccountTransactions(accountId);
    }

    @GetMapping("/login")
    private String login(@RequestParam String username, @RequestParam String password) {
        return completeService.login(username, password);
    }

    @GetMapping("/accounts")
    private List<AccountInfo> allAccounts() {
        return completeService.accountInfos();
    }

    @GetMapping("/isInsured")
    private String isInsured() {
        return completeService.isInsured();
    }

    @PostMapping("/openTravel")
    private String accountOpen(@RequestParam String amount, @RequestParam String period) {
        return completeService.travelInsuranceAccountRouter(amount, period);
    }

    @PostMapping("/transferToTravelAccount")
    private String transferToAccount(@RequestParam String amount) {
        return completeService.fundTransfer(amount, 0) == null ? "Failed" : "Successful";
    }

    @PostMapping("/transferToTravelInsurance")
    private String transferToInsurance(@RequestParam String period) {
        String amount = null;
        if (Objects.equals(period, "3")) amount = "15";
        if (Objects.equals(period, "6")) amount = "25";
        if (Objects.equals(period, "12")) amount = "45";
        return amount != null ? completeService.fundTransfer(amount, Integer.parseInt(period)) == null ? "Failed" : "Successful" : "Invalid Period";
    }
}
