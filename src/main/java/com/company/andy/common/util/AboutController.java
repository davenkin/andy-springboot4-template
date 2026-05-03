package com.company.andy.common.util;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Hidden
@RestController
@RequestMapping
public class AboutController {
    private static final Instant DEPLOYED_TIME = Instant.now();

    @GetMapping(value = "/about")
    public String about() {
        return "Running! Started at " + DEPLOYED_TIME;
    }

    @GetMapping("/favicon.ico")
    public void dummyFavicon() {
        //nop
    }
}
