package com.langly.app.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
}