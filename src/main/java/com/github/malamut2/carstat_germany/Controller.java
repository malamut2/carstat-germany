package com.github.malamut2.carstat_germany;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars")
public class Controller {

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @PostMapping("/retrieveFromKBA")
    public void retrieveStatisticsDataFromKBA() {

    }

}
