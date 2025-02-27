package com.example.online_compiler;

import com.example.online_compiler.domain.codeExecution.CppCodeExecutionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class OnlineCompilerApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(OnlineCompilerApplication.class, args);

        CppCodeExecutionService bean = context.getBean(CppCodeExecutionService.class);

//        String code = "#include<iostream.h>\\nusing namespace std;\\n\\nint main(){\\n\\n  cout<<\\\"hey guys changed how u all doing\\\";\\n  return 0;\\n}";
        String code = "#include <chrono>\n#include <thread>\n\nint main() {\n    using namespace std::this_thread; // sleep_for, sleep_until\n    using namespace std::chrono; // nanoseconds, system_clock, seconds\n\n    sleep_for(nanoseconds(10));\n    sleep_until(system_clock::now() + seconds(10));\n}";

        bean.setCode(code);
        bean.execute();
    }

}



