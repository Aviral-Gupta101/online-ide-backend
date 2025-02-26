package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.util.enums.CodeExecutionTypeEnum;
import com.github.dockerjava.api.model.HostConfig;
import org.springframework.stereotype.Component;

@Component
public class CppCodeExecutionService extends AbstractCodeExecutionService {

    public CppCodeExecutionService() {
        super(CodeExecutionTypeEnum.CPP);
    }

    @Override
    public void execute() throws InterruptedException {

        String code = "#include<iostream>\\nusing namespace std;\\n\\nint main(){\\n\\n  cout<<\\\"hey guys how u all doing\\\";\\n  return 0;\\n}";
        setCode(code);
        setHostConfig(new HostConfig()
                .withCpuCount(1L)
                .withNanoCPUs(500_000_000L)
                .withMemory(512L * 1024 * 1024) // 512MB
                .withMemorySwap(512L * 1024 * 1024)
        );

        CompileAndRunResult compileAndRunResult = runAllTask();
        System.out.println(compileAndRunResult);
    }
}
