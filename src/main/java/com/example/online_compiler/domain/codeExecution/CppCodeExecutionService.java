package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CppCodeExecutionService extends AbstractCodeExecutionService {

    String[] compileAndRunCmd = new String[]{"sh", "-c", "g++ <file_name>.cpp -o <file_name>.out && ./<file_name>.out"};

    public CppCodeExecutionService() {

        super("gcc:latest");
        super.setFileExtension("cpp");
        super.setCodeExecutionTimeout(5);
        super.setCompileAndRunCmd(compileAndRunCmd);
    }

    @Override
    public CompileAndRunResult execute() throws InterruptedException {

        if(getCode() == null || getCode().isEmpty()) {
           throw new IllegalStateException("Error: Cannot execute code. Code is empty");
        }

        CompileAndRunResult compileAndRunResult = super.runAllTask();

        System.out.println(compileAndRunResult.getExitCode());
        System.out.println(compileAndRunResult.getOutput());

        return compileAndRunResult;

    }
}
