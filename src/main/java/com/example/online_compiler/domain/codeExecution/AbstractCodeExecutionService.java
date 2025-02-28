package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractCodeExecutionService {

    @Autowired
    public DockerClient dockerClient;

    private String containerId;

    private final String containerImage;

    @Setter
    @Getter
    private String code; // in base64 format;

    @Setter
    private String input = "";

    @Getter
    private String baseFileName; // Default to Main else change method generateBaseFileName()

    @Setter
    private String fileExtension;

    @Getter
    private String[] compileAndRunCmd;

    @Setter
    private int timeout = 10; // default to 5 seconds;

    @Getter
    @Setter
    private int codeExecutionTimeout = 10; // 15 seconds

    @Setter
    private HostConfig hostConfig;

    public AbstractCodeExecutionService(@NonNull String containerImage) {

        if (containerImage.isBlank())
            throw new IllegalArgumentException("containerImage can't be blank");

        this.containerImage = containerImage;
        this.hostConfig = new HostConfig();

        generateBaseFileName();
    }

    public void setCompileAndRunCmd(String[] compileAndRunCmd) {

        String[] updatedCompileAndRunCmd = Arrays.copyOf(compileAndRunCmd, compileAndRunCmd.length);

        for (int i = 0; i < updatedCompileAndRunCmd.length; i++) {
            updatedCompileAndRunCmd[i] = updatedCompileAndRunCmd[i].replaceAll("<file_name>", baseFileName);
        }

        this.compileAndRunCmd = updatedCompileAndRunCmd;
    }

    private void startContainer() {

        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(containerImage)
                .withHostConfig(hostConfig)
                .withTty(true)
                .exec();

        dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        this.containerId = createContainerResponse.getId();
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
        CompletableFuture.runAsync(() -> {
            try {
                dockerClient.stopContainerCmd(containerId).exec();
                dockerClient.removeContainerCmd(containerId).exec();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("ERROR: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restore interrupt flag
            }
        });


    }

    protected CompileAndRunResult runAllTask() throws InterruptedException {

        try {

            startContainer();

            boolean containerRunning = isContainerRunning(3, timeout);

            if (!containerRunning) {
                throw new RuntimeException("ERROR: Container is not running");
            }

            sendCodeToContainer();
            return compileAndRun();

        } catch (Exception e) {

            log.error("ERROR: Unable to execute runAllTask{}", e.getMessage());
            throw new RuntimeException(e);

        } finally {
            removeContainer();
        }

    }

    public abstract CompileAndRunResult execute() throws InterruptedException;
}
