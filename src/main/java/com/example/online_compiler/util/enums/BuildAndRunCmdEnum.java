package com.example.online_compiler.util.enums;

public enum BuildAndRunCmdEnum {

    CPP(new String[]{"sh", "-c", "g++ /<file_name>.cpp -o /<file_name>.out 2>&1 && /<file_name>.out 2>&1"});

    final String[] cmd;

    BuildAndRunCmdEnum(String[] cmd) {
        this.cmd = cmd;
    }

    public String[] getRawCmd() {
        return cmd;
    }

    public String[] getCmd(String fileNameWithoutExtension) {

        String[] cmd2 = new String[cmd.length];

        for(int i = 0; i < cmd.length; i++) {
            cmd2[i] = cmd[i].replaceAll("<file_name>", fileNameWithoutExtension);
        }

        return cmd2;
    }
}
