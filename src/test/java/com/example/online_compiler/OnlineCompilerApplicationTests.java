package com.example.online_compiler;

import com.example.online_compiler.DTO.RunCodeDto;
import com.example.online_compiler.domain.codeExecution.CppCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.JavaCodeExecutionService;
import com.example.online_compiler.domain.codeExecution.PythonCodeExecutionService;
import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.service.OnlineCompilerService;
import com.example.online_compiler.service.StatusService;
import com.example.online_compiler.util.CompilerTypeEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OnlineCompilerApplicationTests {

    @Autowired
    OnlineCompilerService onlineCompilerService;

    @Autowired
    private PythonCodeExecutionService pythonCodeExecutionService;

    @Autowired
    private CppCodeExecutionService cppCodeExecutionService;

    @Autowired
    private JavaCodeExecutionService javaCodeExecutionService;

    @BeforeEach
    public void init() throws Exception {

        if(!pythonCodeExecutionService.isImageExists())
            pythonCodeExecutionService.pullImage();

        if(!cppCodeExecutionService.isImageExists())
            cppCodeExecutionService.pullImage();

        if(!javaCodeExecutionService.isImageExists())
            javaCodeExecutionService.pullImage();
    }

    @Test
    @DisplayName("Test CPP Compilation")
    public void test1(){

        String cppCode = "#include <iostream>\nusing namespace std;\n\nint main() {\n\n    int a = 10; \n    int b = 20;\n\n    cin>>a>>b;\n\n    cout<<\"A: \"<<a<<endl;\n    cout<<\"B: \"<<b<<endl;\n\n    cout<<\"SUM: \" <<a+b<<endl;\n    return 0;\n}";
        String input = "10 20";

        RunCodeDto runCodeDto = new RunCodeDto();

        runCodeDto.setCompilerType(CompilerTypeEnum.CPP);
        runCodeDto.setInput(input);
        runCodeDto.setCode(cppCode);

        String expectedCodeOutput = "A: 10\nB: 20\nSUM: 30\n";
        CompileAndRunResult expectedResult = new CompileAndRunResult(0L, expectedCodeOutput);

        CompileAndRunResult actualResult = onlineCompilerService.runCode(runCodeDto);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test JAVA Compilation")
    public void test2(){

        String javaCode = "class Main {\r\n    public static void main(String[] args) {\r\n        System.out.println(\"Hello, World!\"); \r\n    }\r\n}";
        String input = "";

        RunCodeDto runCodeDto = new RunCodeDto();

        runCodeDto.setCompilerType(CompilerTypeEnum.JAVA);
        runCodeDto.setInput(input);
        runCodeDto.setCode(javaCode);

        String expectedCodeOutput = "Hello, World!\n";
        CompileAndRunResult expectedResult = new CompileAndRunResult(0L, expectedCodeOutput);

        CompileAndRunResult actualResult = onlineCompilerService.runCode(runCodeDto);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test Python3 Compilation")
    public void test3(){

        String pythonCode = "for i in range(0, 10):\n  print(\"i: \", i)";

        RunCodeDto runCodeDto = new RunCodeDto();
        runCodeDto.setCompilerType(CompilerTypeEnum.PYTHON);
        runCodeDto.setInput("");
        runCodeDto.setCode(pythonCode);

        String expectedCodeOutput = "i:  0\ni:  1\ni:  2\ni:  3\ni:  4\ni:  5\ni:  6\ni:  7\ni:  8\ni:  9\n";
        CompileAndRunResult expectedResult = new CompileAndRunResult(0L, expectedCodeOutput);

        CompileAndRunResult actualResult = onlineCompilerService.runCode(runCodeDto);

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
