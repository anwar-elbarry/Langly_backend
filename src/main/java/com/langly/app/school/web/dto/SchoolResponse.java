package com.langly.app.school.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolResponse {
    private String id;
    private String name;
    private String logo;
    private String city;
    private String country;
    private String address;
    private String status;
}
