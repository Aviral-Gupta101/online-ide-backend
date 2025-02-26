package com.example.online_compiler.domain.codeExecution;

import com.example.online_compiler.entity.CompileAndRunResult;
import com.example.online_compiler.util.enums.BuildAndRunCmdEnum;
import com.example.online_compiler.util.enums.CodeExecutionTypeEnum;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.util.UUID;


@Slf4j
public abstract class AbstractCodeExecutionService {

    @Autowired
    public DockerClient dockerClient;

    private final CodeExecutionTypeEnum codeExecutionType;

    @NonNull
    @Setter
    private HostConfig hostConfig;

    @NonNull
    @Setter
    private String code;

    private String fileNameWithExtension;
    private String containerId;

    String[] command;

    public AbstractCodeExecutionService(CodeExecutionTypeEnum codeExecutionType) {

        this.codeExecutionType = codeExecutionType;
        this.hostConfig = new HostConfig();

        generatedFileNameWithExtension();
        generateCompileAndRunCommand();
    }

    /**
     * Creates a container of image specified in codeExecutionTypeEnum, with default host configuration
     * 1 CPU Core, 512 MB RAM and 512 MB SWAP
     */
    private void startContainer() {

        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(codeExecutionType.getImage())
                .withHostConfig(hostConfig)
                .withTty(true)
                .exec();

        dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        this.containerId = createContainerResponse.getId();
    }

    private void generatedFileNameWithExtension() {

        if (codeExecutionType == CodeExecutionTypeEnum.CPP)
            this.fileNameWithExtension = UUID.randomUUID().toString().substring(0, 8) + ".cpp";

        else
            throw new RuntimeException("Error: CodeExecutionTypeEnum is not defined for generating filenames");
    }

    private void generateCompileAndRunCommand() {

        if (codeExecutionType == CodeExecutionTypeEnum.CPP) {
            command = BuildAndRunCmdEnum.CPP.getCmd(fileNameWithExtension.split("\\.")[0]);
        }

        else
            throw new RuntimeException("Error: CodeExecutionTypeEnum is not defined for generating command");


    }

    /**
     * @param pollingInterval Time interval to check status of container in seconds
     * @param timeout         Max duration to wait in seconds
     * @return Boolean value true if container is running else false
     * @throws InterruptedException Throws interruptedException if thread is interrupted
     */
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

    protected void sendCodeToContainer() throws InterruptedException {

        boolean containerRunning = isContainerRunning(5, 15);

        if (!containerRunning) {
            throw new RuntimeException("ERROR: Unable to send code because Container is not running");
        }

        String[] command = new String[]{"sh", "-c", "echo \"" + code + "\" > /" + fileNameWithExtension};

        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command)
                .exec();

        dockerClient.execStartCmd(execResponse.getId()).exec(new ResultCallback.Adapter<>()).awaitCompletion();
    }

    private CompileAndRunResult compileAndRun() throws InterruptedException {

        boolean containerRunning = isContainerRunning(2, 5);

        if (!containerRunning) {
            throw new RuntimeException("ERROR: Unable to compile and run because Container is not running");
        }

        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command)
                .exec();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        dockerClient.execStartCmd(execResponse.getId())
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            outputStream.write(frame.getPayload());
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    }
                }).awaitCompletion();

        String output = outputStream.toString();
        Long exitCodeLong = dockerClient.inspectExecCmd(execResponse.getId()).exec().getExitCodeLong();

        return new CompileAndRunResult(exitCodeLong, output);
    }

    private void removeContainer() {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

    }

    protected CompileAndRunResult runAllTask() throws InterruptedException {

        if (code.isBlank()) {
            throw new RuntimeException("ERROR: Code is blank");
        }

        startContainer();
        sendCodeToContainer();
        var result = compileAndRun();
        removeContainer();

        return result;

    }

    public abstract void execute() throws InterruptedException;

}
