package com.example.online_compiler.controller;

import com.example.online_compiler.Debug;
import com.example.online_compiler.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    StatusService statusService;

    @Autowired
    Debug debug;

    @GetMapping("")
    public ResponseEntity<?> getStatus(){

        boolean dindServiceStatus = statusService.dindServiceStatus();

        return ResponseEntity.ok(Map.of(
                "Dind Service Connected", dindServiceStatus,
                "Dind Service Address", debug.getDockerDindHost()
        ));
    }
}
