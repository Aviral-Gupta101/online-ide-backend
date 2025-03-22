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

        if (dockerDindHost == null || dockerDindHost.isEmpty()) {
            throw new RuntimeException("Docker host not set");
        }

        boolean isTlsEnabled = System.getenv("DOCKER_TLS_VERIFY") != null &&
                "1".equals(System.getenv("DOCKER_TLS_VERIFY"));
        String certPath = System.getenv("DOCKER_CERT_PATH");

        System.out.println("TLS: " + isTlsEnabled);
        System.out.println("CERT PATH: " + certPath);

        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerDindHost);

        if (isTlsEnabled && certPath != null && !certPath.isEmpty()) {
            configBuilder.withDockerTlsVerify(true)
                    .withDockerCertPath(certPath);
        } else {
            configBuilder.withDockerTlsVerify(false);
        }

        DefaultDockerClientConfig config = configBuilder.build();

        DockerHttpClient client = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig()) // Ensure SSL config is applied
                .build();;

        return DockerClientImpl.getInstance(config, client);
    }
}
