package com.michaelw.tutorials.springbootoauth2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mwangia on 11/20/19.
 */
@RestController
@RequestMapping("/greetings")
public class GreetingController {

    @PostMapping(path = "/{name}")
    public ResponseEntity<String> getGreeting(@PathVariable("name") String name){
        String fullGreeting  = "Hello, " + name;
        return new ResponseEntity<>(fullGreeting, HttpStatus.OK);

    }
}
