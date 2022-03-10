package com.deekol.protRenderFarm.server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userEntity")
    private List<TaskEntity> taskEntities;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
