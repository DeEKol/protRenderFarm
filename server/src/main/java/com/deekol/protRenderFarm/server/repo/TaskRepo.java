package com.deekol.protRenderFarm.server.repo;

import com.deekol.protRenderFarm.server.domain.TaskEntity;
import com.deekol.protRenderFarm.server.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepo extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserEntity(UserEntity userEntity);
    List<TaskEntity> findByStatusAndUserEntity(String status, UserEntity userEntity);
}
