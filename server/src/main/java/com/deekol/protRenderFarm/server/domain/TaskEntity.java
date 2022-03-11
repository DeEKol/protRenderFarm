package com.deekol.protRenderFarm.server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;

/*
Как понял колонка с description или name не нужна.
Создал startRender и finishRender для сохранения истории смены статусов.
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

    @Column(name = "start_render")
    private Calendar startRender = Calendar.getInstance();
    @Column(name = "finish_render")
    private Calendar finishRender;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public TaskEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
