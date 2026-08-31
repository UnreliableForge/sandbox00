package com.unreliableforge.sandbox00.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map<String,String> health() {

        log.info("/health");

        return Map.of("status", "OK");
        
    }
}
