package pl.rotkom.friday.thirdparty.todoist.api.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaskDTO {

    private String id;

    @JsonProperty("project_id")
    private String projectId;

    private String content;

    @JsonProperty("created_at")
    private Instant createdAt;
}
