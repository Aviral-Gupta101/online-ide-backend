package com.example.online_compiler.service;

import com.example.online_compiler.DTO.RunCodeDto;
import com.example.online_compiler.domain.codeExecution.AbstractCodeExecutionService;
import com.example.online_compiler.domain.factory.CodeExecutionFactory;
import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.exception.customExceptions.UnableToRunCodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OnlineCompilerService {

    @Autowired
    CodeExecutionFactory codeExecutionFactory;

    public CompileAndRunResult runCode(RunCodeDto runCodeDto) {

        AbstractCodeExecutionService codeExecutionService = codeExecutionFactory.getCodeExecutionServices(runCodeDto.getCompilerType());
        try {
            codeExecutionService.setInput(runCodeDto.getInput());
            String base64Code = Base64.encodeBase64String(runCodeDto.getCode().getBytes());
            codeExecutionService.setCode(base64Code);
            return codeExecutionService.setup();

        } catch (Exception e) {

            log.warn("Run code failed", e);
            throw new UnableToRunCodeException(e.getMessage());
        }
    }
}
