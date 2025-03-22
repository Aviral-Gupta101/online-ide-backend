package com.example.online_compiler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Debug {

    @Value("${app.docker_host}")
    private String dockerDindHost;

    public void debug(){
        System.out.println("dockerDindHost: " + dockerDindHost);
    }

    public String getDockerDindHost() {
        return dockerDindHost;
    }
}
