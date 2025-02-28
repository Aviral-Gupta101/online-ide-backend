package com.example.online_compiler.service;

import com.example.online_compiler.DTO.RunCodeDto;
import com.example.online_compiler.domain.codeExecution.AbstractCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.CppCodeExecutionService;
import com.example.online_compiler.entity.CompileAndRunResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DockerService {

    @Autowired
    CppCodeExecutionService cppCodeExecutionService;

    public CompileAndRunResult runCode(RunCodeDto runCodeDto) {

        try{
            cppCodeExecutionService.setInput(runCodeDto.getInput());
            String base64Code = Base64.encodeBase64String(runCodeDto.getCode().getBytes());
            cppCodeExecutionService.setCode(base64Code);
            return cppCodeExecutionService.execute();

        } catch (Exception e){

            log.warn("Run code failed", e);
            return null;
        }
    }

}
