package com.unreliableforge.sandbox00.backend.domain.extension.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileEntity {
    private String sub;
    private String email;
    private String name;
}
