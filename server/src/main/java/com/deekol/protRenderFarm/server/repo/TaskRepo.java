package com.deekol.protRenderFarm.server.repo;

import com.deekol.protRenderFarm.server.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepo extends JpaRepository<TaskEntity, Long> {
}
