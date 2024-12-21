package pl.rotkom.friday.thirdparty.todoist.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import io.netty.handler.codec.http.HttpStatusClass;
import pl.rotkom.friday.library.json.JsonMapper;
import pl.rotkom.friday.thirdparty.todoist.TodoistConfig;
import pl.rotkom.friday.thirdparty.todoist.api.dto.NewTaskDTO;
import pl.rotkom.friday.thirdparty.todoist.api.dto.ProjectDTO;
import pl.rotkom.friday.thirdparty.todoist.api.dto.TaskDTO;
import pl.rotkom.friday.thirdparty.todoist.api.service.ITodoistApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;

@Component
public class TodoistApi implements ITodoistApi {

    private final HttpClient httpClient;

    public TodoistApi(TodoistConfig todoistConfig) {

        this.httpClient = HttpClient.create()
                .baseUrl("https://api.todoist.com/rest/v2")
                .headers(h -> {
                    h.add("Authorization", "Bearer " + todoistConfig.getToken());
                    h.add("Content-Type", "application/json");
                })
                .headersWhen(h -> Mono.fromSupplier(() -> h.add("X-Request-Id", UUID.randomUUID().toString())));
//                .wiretap(TodoistApi.class.getName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    }

    @Override
    public ProjectDTO getProjectByName(String projectName) {
        return httpClient
                .get()
                .uri("/projects")
                .responseSingle((resp, bb) -> {
                    if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                        return bb.asString();
                    } else {
                        return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error while getProjectByName: " + body)));
                    }
                })
                .map(body -> JsonMapper.deserialize(body, new TypeReference<List<ProjectDTO>>() {}))
                .flatMapMany(Flux::fromIterable)
                .filter(project -> project.getName().equals(projectName))
                .blockFirst();
    }

    @Override
    public ProjectDTO getProjectById(String projectId) {
        return httpClient
                .get()
                .uri("/projects/" + projectId)
                .responseSingle((resp, bb) -> {
                    if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                        return bb.asString();
                    } else {
                        return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error while getProjectById: " + body)));
                    }
                })
                .map(body -> JsonMapper.deserialize(body, ProjectDTO.class))
                .block();
    }

    @Override
    public List<TaskDTO> getTasksByProjectIds(List<String> projectIds) {
        return Flux.fromIterable(projectIds)
                .flatMap(this::getTasksByProjectId, 10)
                .flatMap(Flux::fromIterable)
                .collectList()
                .blockOptional().orElse(Collections.emptyList());
    }

    private Mono<List<TaskDTO>> getTasksByProjectId(String projectId) {
        return httpClient
                .get()
                .uri("/tasks?project_id=" + projectId)
                .responseSingle((resp, bb) -> {
                    if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                        return bb.asString();
                    } else {
                        return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error while getTasksByProjectId: " + body)));
                    }
                })
                .map(body -> JsonMapper.deserialize(body, new TypeReference<List<TaskDTO>>() {}));
    }
    // create tasks
    @Override
    public List<TaskDTO> createTasks(List<NewTaskDTO> newTaskDTOs) {
        return Flux.fromIterable(newTaskDTOs)
                .flatMap(this::createTask, 10)
                .collectList()
                .blockOptional().orElse(Collections.emptyList());
    }

    // create task
    private Mono<TaskDTO> createTask(NewTaskDTO newTaskDTO) {
        return httpClient
                .post()
                .uri("/tasks")
                .send(ByteBufFlux.fromString(Mono.just(JsonMapper.serialize(newTaskDTO))))
                .responseSingle((resp, bb) -> {
                    if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                        return bb.asString();
                    } else {
                        return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error while createTask: " + body)));
                    }
                })
                .map(body -> JsonMapper.deserialize(body, TaskDTO.class));
    }

    // complete task
    @Override
    public void completeTask(String taskId) {
        httpClient
                .post()
                .uri("/tasks/" + taskId + "/close")
                .send(ByteBufMono.empty())
                .responseSingle((resp, bb) -> {
                    if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                        return bb.asString();
                    } else {
                        return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error while completeTask: " + body)));
                    }
                })
                .block();
    }
}
