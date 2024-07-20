package com.hackathon.hack_attack.controller;

import com.hackathon.hack_attack.service.CompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class CompleteController {
    @Autowired
    private CompleteService completeService;

    @GetMapping
    private Object returnAccount(@RequestBody String accountId) {
        return completeService.fetch(accountId);
    }
}
