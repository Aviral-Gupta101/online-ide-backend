package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CppCodeExecutionService extends AbstractCodeExecutionService {

    private final String[] compileAndRunCmd = new String[]{"sh", "-c", "g++ <file_name>.cpp -o <file_name>.out && ./<file_name>.out < input.txt"};

    public CppCodeExecutionService() {
        super("gcc:latest");
    }

    @Override
    public CompileAndRunResult setup() throws InterruptedException {

        super.setFileExtension("cpp");
        super.setCodeExecutionTimeout(5);
        super.setCompileAndRunCmd(compileAndRunCmd);
        
        return super.execute();
    }
}
