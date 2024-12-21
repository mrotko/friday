package pl.rotkom.friday.thirdparty.todoist.api.service;

import java.util.List;

import pl.rotkom.friday.thirdparty.todoist.api.dto.NewTaskDTO;
import pl.rotkom.friday.thirdparty.todoist.api.dto.ProjectDTO;
import pl.rotkom.friday.thirdparty.todoist.api.dto.TaskDTO;


public interface ITodoistApi {

    ProjectDTO getProjectByName(String projectName);

    ProjectDTO getProjectById(String projectId);

    List<TaskDTO> getTasksByProjectIds(List<String> projectIds);

    List<TaskDTO> createTasks(List<NewTaskDTO> newTaskDTOs);

    void completeTask(String taskId);
}
