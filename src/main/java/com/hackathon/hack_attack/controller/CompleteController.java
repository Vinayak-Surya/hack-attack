package com.hackathon.hack_attack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hackathon.hack_attack.service.CompleteService;

@RequestMapping
@RestController
public class CompleteController {
    @Autowired
    private CompleteService completeService;

    @GetMapping
    private Object returnAccount() {
        return completeService.fetch();
    }
}
