package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.exception.customExceptions.ContainerImageNotFoundException;
import com.example.online_compiler.exception.customExceptions.ContainerNotRunningException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class AbstractCodeExecutionService {

    @Autowired
    private DockerClient dockerClient;

    private String containerId;

    private final String containerImage;

    private static final int MAX_INSTANCE_LIMIT = 5; // Max no containers at a time
    private static final AtomicInteger maxInstanceCounter = new AtomicInteger(MAX_INSTANCE_LIMIT);
    private static final Lock lock = new ReentrantLock();
    private static final Condition lockCondition = lock.newCondition();

    @Setter
    @Getter
    private String code; // in base64 format;

    @Setter
    private String input = "";

    @Getter(AccessLevel.PACKAGE)
    private String baseFileName; // Default to Main else change method generateBaseFileName()

    @Setter(AccessLevel.PACKAGE)
    private String fileExtension;

    @Getter
    private String[] compileAndRunCmd;

    @Setter(AccessLevel.PACKAGE)
    private int timeout = 10; // default to 5 seconds;

    @Getter
    @Setter
    private int codeExecutionTimeout = 10; // 15 seconds

    @Setter(AccessLevel.PACKAGE)
    private HostConfig hostConfig;

    public AbstractCodeExecutionService(@NonNull String containerImage) {

        if (containerImage.isBlank())
            throw new IllegalArgumentException("containerImage can't be blank");

        this.containerImage = containerImage;
        this.hostConfig = new HostConfig();

        generateBaseFileName();
    }

    public boolean isImageExists() {

        List<Image> images = dockerClient.listImagesCmd().exec();

        return images.stream()
                .flatMap(image -> Arrays.stream(image.getRepoTags() != null ? image.getRepoTags() : new String[0]))
                .anyMatch(tag -> tag.startsWith(containerImage));

    }

    public void pullImage() throws InterruptedException {

        if (isImageExists())
            return;

        String image = containerImage.split(":")[0];
        String tag = containerImage.split(":")[1];

        try {
            dockerClient.pullImageCmd(image)
                    .withTag(tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        } catch (Exception e) {
            System.err.println("Failed to pull image: " + e.getMessage());
        }
    }

    public void setCompileAndRunCmd(String[] compileAndRunCmd) {

        String[] updatedCompileAndRunCmd = Arrays.copyOf(compileAndRunCmd, compileAndRunCmd.length);

        for (int i = 0; i < updatedCompileAndRunCmd.length; i++) {
            updatedCompileAndRunCmd[i] = updatedCompileAndRunCmd[i].replaceAll("<file_name>", baseFileName);
        }

        this.compileAndRunCmd = updatedCompileAndRunCmd;
    }

    private void startContainer() throws InterruptedException {

        lock.lock();

        try {

            while (maxInstanceCounter.get() == 0) {
                lockCondition.await();
            }

            maxInstanceCounter.decrementAndGet();

            CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(containerImage)
                    .withHostConfig(hostConfig)
                    .withTty(true)
                    .exec();

            dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
            this.containerId = createContainerResponse.getId();

        } finally {
            lock.unlock();
        }
    }

    private void generateBaseFileName() {
//        this.baseFileName = UUID.randomUUID().toString().substring(0, 8);
        this.baseFileName = "Main";
    }

    private boolean isContainerRunning(int pollingInterval, int timeout) throws InterruptedException {

        InspectContainerResponse.ContainerState state = dockerClient.inspectContainerCmd(containerId).exec().getState();

        if (dockerClient.inspectContainerCmd(containerId).exec().getState() == null) {
            log.warn("Container State is NULL");
            return false;
        } else if (state.getDead() != null && state.getDead()) {
            log.warn("Container is Dead");
            return false;
        } else if (state.getOOMKilled() != null && state.getOOMKilled()) {

            log.warn("Container is OOM Killed, not enough memory to start the container");
            return false;
        }

        while ((state.getRunning() == null || !state.getRunning()) && timeout > 0) {

            Thread.sleep(pollingInterval * 1000L);
            timeout -= pollingInterval;
        }

        if (state.getRunning() == null || !state.getRunning()) {
            log.warn("Container cannot be started");
            return false;
        }

        return true;
    }

    private void sendCodeToContainer() throws InterruptedException {

        if (fileExtension == null || fileExtension.isEmpty())
            throw new IllegalArgumentException("fileExtension can't be blank");

        if (code == null || code.isEmpty())
            throw new IllegalArgumentException("code can't be blank");

        String[] command = new String[]{
                "sh", "-c",
                "echo " + code + " | base64 -d > /" + baseFileName + "." + fileExtension
                        + " && echo \"" + input + "\" > /input.txt"
        };

        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command)
                .exec();

        dockerClient.execStartCmd(execResponse.getId()).exec(new ResultCallback.Adapter<>()).awaitCompletion();
    }

    private CompileAndRunResult compileAndRun() throws InterruptedException {

        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(compileAndRunCmd)
                .exec();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        boolean result = dockerClient.execStartCmd(execResponse.getId())
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            outputStream.write(frame.getPayload());
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    }
                    // +2 seconds for extra overhead of starting the container etc.
                }).awaitCompletion(codeExecutionTimeout + 2, TimeUnit.SECONDS);

        if (!result)
            return new CompileAndRunResult(-1L, "TIME LIMIT EXCEEDED");

        String output = outputStream.toString();
        Long exitCodeLong = dockerClient.inspectExecCmd(execResponse.getId()).exec().getExitCodeLong();

        return new CompileAndRunResult(exitCodeLong, output);
    }

    private void removeContainer() {

        // Make the container removal operation asynchronous
        CompletableFuture.runAsync(() -> {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
        }).thenRun(() -> {
            lock.lock();
            try {
                maxInstanceCounter.incrementAndGet();
                lockCondition.signal();
            } finally {
                lock.unlock();
            }
        });
    }

    protected CompileAndRunResult execute() {

        try {

            if(!isImageExists()){

                CompletableFuture.runAsync(() -> {
                    try {
                        pullImage();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

                throw new ContainerImageNotFoundException("Container image does not exits, please try again after sometime");
            }

            startContainer();

            boolean containerRunning = isContainerRunning(3, timeout);

            if (!containerRunning) {
                throw new ContainerNotRunningException("ERROR: Container not in running state");
            }

            sendCodeToContainer();
            return compileAndRun();

        } catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {

            log.error("ERROR: Unable to execute code {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            removeContainer();
        }

    }

    public abstract CompileAndRunResult setup() throws InterruptedException;
}
