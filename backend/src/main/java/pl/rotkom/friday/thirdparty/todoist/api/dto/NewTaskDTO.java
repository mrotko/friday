package pl.rotkom.friday.thirdparty.todoist.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NewTaskDTO {

    private String content;

    private String description;

    private List<String> labels;

    @JsonProperty("project_id")
    private String projectId;

    private Integer priority;

    @JsonProperty("due_string")
    private String dueString;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("due_datetime")
    // RFC3339
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime dueDateTime;

    private Integer duration;

    @JsonProperty("duration_unit")
    private String durationUnit; // minute, day
}
