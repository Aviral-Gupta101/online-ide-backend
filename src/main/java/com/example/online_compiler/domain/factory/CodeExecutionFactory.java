package com.example.online_compiler.domain.factory;

import com.example.online_compiler.domain.codeExecution.AbstractCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.CppCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.JavaCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.PythonCodeExecutionService;
import com.example.online_compiler.util.CompilerTypeEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeExecutionFactory {

    @Autowired
    CppCodeExecutionService cppCodeExecutionService;

    @Autowired
    PythonCodeExecutionService pythonCodeExecutionService;

    @Autowired
    JavaCodeExecutionService javaCodeExecutionService;

    public AbstractCodeExecutionService getCodeExecutionServices(@NotNull CompilerTypeEnum compilerType) {

        if(compilerType == CompilerTypeEnum.PYTHON)
            return pythonCodeExecutionService;

        else if(compilerType == CompilerTypeEnum.CPP)
            return cppCodeExecutionService;

        else if (compilerType == CompilerTypeEnum.JAVA)
            return javaCodeExecutionService;

        throw new IllegalArgumentException("Unsupported compiler type: " + compilerType);
    }

}
