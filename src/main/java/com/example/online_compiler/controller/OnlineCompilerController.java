package com.example.online_compiler.controller;

import com.example.online_compiler.DTO.RunCodeDto;
import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.service.OnlineCompilerService;
import com.example.online_compiler.util.RequestValidatorUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/online-compiler")
public class OnlineCompilerController {

    @Autowired
    OnlineCompilerService onlineCompilerService;

    @PostMapping("/run-code")
    public ResponseEntity<?> runCode(@Valid @RequestBody RunCodeDto runCodeDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return RequestValidatorUtil.getAllErrors(bindingResult);
        }

        CompileAndRunResult compileAndRunResult = onlineCompilerService.runCode(runCodeDto);
        return ResponseEntity.ok(compileAndRunResult);
    }

}
