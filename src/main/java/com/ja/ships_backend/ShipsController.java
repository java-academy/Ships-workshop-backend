package com.ja.ships_backend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ShipsController {

    @RequestMapping("/")
    String home() {
        return "Welcome to Ships Backend";
    }
}
