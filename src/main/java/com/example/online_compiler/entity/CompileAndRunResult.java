package com.example.online_compiler.entity;

import lombok.*;

@Data
@RequiredArgsConstructor
public class CompileAndRunResult {

    @NonNull
    Long exitCode;

    @NonNull
    String output;

    public boolean isError(){
        return exitCode != 0;
    }

}
