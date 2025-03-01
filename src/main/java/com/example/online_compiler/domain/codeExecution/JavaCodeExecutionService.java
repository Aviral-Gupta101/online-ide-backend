package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class JavaCodeExecutionService extends AbstractCodeExecutionService {

    private final String[] compileAndRunCmd = new String[]{"sh", "-c", "java <file_name>.java < input.txt"};

    public JavaCodeExecutionService() {
        super("openjdk:latest");
    }

    @Override
    public CompileAndRunResult setup() throws InterruptedException {

        super.setFileExtension("java");
        super.setCodeExecutionTimeout(5);
        super.setCompileAndRunCmd(compileAndRunCmd);

        return super.execute();
    }
}
