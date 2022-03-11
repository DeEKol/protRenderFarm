package com.deekol.protRenderFarm.server.service;

import com.deekol.protRenderFarm.server.domain.TaskEntity;
import com.deekol.protRenderFarm.server.domain.UserEntity;
import com.deekol.protRenderFarm.server.repo.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TaskService {
    private final TaskRepo taskRepo;

    public TaskService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public void save(TaskEntity taskEntity) {
        taskRepo.save(taskEntity);
    }

    public List<TaskEntity> findByUserEntity(UserEntity userEntity) {
        return taskRepo.findByUserEntity(userEntity);
    }

    public List<TaskEntity> findByStatusAndUserEntity(String status, UserEntity userEntity) {
        return taskRepo.findByStatusAndUserEntity(status, userEntity);
    }

}
