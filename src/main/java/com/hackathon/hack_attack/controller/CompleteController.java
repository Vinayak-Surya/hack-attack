package com.hackathon.hack_attack.controller;

import com.hackathon.hack_attack.service.CompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class CompleteController {
    @Autowired
    private CompleteService completeService;

    @GetMapping
    @CrossOrigin(origins = "*")
    private Object returnAccount(@RequestBody String accountId) {
        return completeService.fetch(accountId);
    }
}
