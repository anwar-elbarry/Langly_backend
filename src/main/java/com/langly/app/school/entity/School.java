package com.langly.app.school.entity;

import com.langly.app.finance.entity.Subscription;
import com.langly.app.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import com.langly.app.course.entity.enums.SchoolStatus;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data

@Entity
@Table(name = "schools")
@NoArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String subDomain;
    private String logo;
    private String city;
    private String country;
    private String address;
    private SchoolStatus status;

    @OneToMany(mappedBy = "school",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<User> users = new ArrayList<>();

    @OneToMany
    private List<Subscription> subscriptions = new ArrayList<>();
}
