package com.example.online_compiler;

import com.example.online_compiler.domain.codeExecution.CppCodeExecutionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class OnlineCompilerApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(OnlineCompilerApplication.class, args);

        CppCodeExecutionService bean = context.getBean(CppCodeExecutionService.class);

    }

}



