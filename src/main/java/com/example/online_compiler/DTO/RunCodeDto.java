package com.example.online_compiler.DTO;

import com.example.online_compiler.util.CompilerTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RunCodeDto {

    @NotNull(message = "CompilerType cannot be null")
    private CompilerTypeEnum compilerType;

    @NotNull(message = "Code cannot be null")
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotNull(message = "Input cannot be null")
    private String input = "";
}
