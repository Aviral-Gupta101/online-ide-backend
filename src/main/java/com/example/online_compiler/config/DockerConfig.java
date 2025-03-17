package com.example.online_compiler.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Value("${app.dind_service}")
    private String dockerDindHost;

    @Bean
    public DockerClient dockerClient() {

        if(dockerDindHost == null || dockerDindHost.isEmpty())
            throw new RuntimeException("Docker host not set");

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerDindHost)
                .build();

        DockerHttpClient client = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();
        return DockerClientImpl.getInstance(config, client);
    }
}
