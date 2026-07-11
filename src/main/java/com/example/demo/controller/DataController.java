package com.example.demo.controller;

import com.example.demo.security.CustomUserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @PostMapping("/command")
    public DataResponse handleCommand(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody Command command) {
        if (command == null || command.steps() == null) {
            return new DataResponse(0.0, 0.0, 0.0);
        }

        Steps steps = command.steps();
        return new DataResponse(steps.xCoord(), steps.yCoord(), steps.zCoord());
    }

    public record Command(String cmd, Steps steps) {
    }

    public record Steps(double xCoord, double yCoord, double zCoord, double toolSafetySteps) {
    }

    public record DataResponse(double xCoord, double yCoord, double zCoord) {
    }
}