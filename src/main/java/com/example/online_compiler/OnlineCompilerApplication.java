package com.example.online_compiler;

import com.github.dockerjava.api.DockerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class OnlineCompilerApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(OnlineCompilerApplication.class, args);

        DockerClient dockerClient = context.getBean(DockerClient.class);
        Debug bean = context.getBean(Debug.class);
        bean.debug();

//        try {
//            Thread.sleep(5000);
//            dockerClient.pingCmd().exec();
//            System.out.println("Connected: Docker DIND");
//        } catch (Exception e) {
//            System.err.println("Failed to connect to Docker DinD: " + e.getMessage());
//            System.exit(1); // Stop application
//        }
    }
}



