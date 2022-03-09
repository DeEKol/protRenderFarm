package com.deekol.protRenderFarm.server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*
Как понял колонка с description или name не нужна
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String status = "RENDERING";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public TaskEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
