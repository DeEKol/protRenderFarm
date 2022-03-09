package com.deekol.protRenderFarm.server.repo;

import com.deekol.protRenderFarm.server.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    //Метод для поиска по имени
    UserEntity findByUsername(String username);
}
