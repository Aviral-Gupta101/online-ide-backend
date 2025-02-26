package com.example.online_compiler.util.enums;

import lombok.Getter;

@Getter
public enum CodeExecutionTypeEnum {
    CPP("gcc:latest");

    private final String image;

    CodeExecutionTypeEnum(String image) {
        this.image = image;
    }

}
