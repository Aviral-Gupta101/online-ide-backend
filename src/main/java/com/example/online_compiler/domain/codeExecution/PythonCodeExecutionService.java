package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class PythonCodeExecutionService extends AbstractCodeExecutionService {

    private final String[] compileAndRunCmd = new String[]{"sh", "-c", "python3 /<file_name>.py < input.txt"};

    public PythonCodeExecutionService() {
        super("python:3.14.0a5-alpine3.21");
    }

    @Override
    public CompileAndRunResult setup() throws InterruptedException {

        super.setFileExtension("py");
        super.setCodeExecutionTimeout(5);
        super.setCompileAndRunCmd(compileAndRunCmd);

        return super.execute();
    }
}
