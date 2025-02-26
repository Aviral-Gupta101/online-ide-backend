package com.example.online_compiler.util.enums;

import lombok.Getter;

@Getter
public enum CodeFileExtensionEnum {

    cpp("cpp");

    private final String extension;

    CodeFileExtensionEnum(String extension) {
        this.extension = extension;
    }
}
