package com.unreliableforge.sandbox00.backend.domain.generated.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

public class Users {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.938255338+09:00", comments="Source field: users.sub")
    private String sub;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.9416264+09:00", comments="Source field: users.email")
    private String email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941818533+09:00", comments="Source field: users.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941971049+09:00", comments="Source field: users.created_at")
    private LocalDateTime createdAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.940436421+09:00", comments="Source field: users.sub")
    public String getSub() {
        return sub;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941491127+09:00", comments="Source field: users.sub")
    public void setSub(String sub) {
        this.sub = sub;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.94169325+09:00", comments="Source field: users.email")
    public String getEmail() {
        return email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941769939+09:00", comments="Source field: users.email")
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941867368+09:00", comments="Source field: users.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.941924038+09:00", comments="Source field: users.name")
    public void setName(String name) {
        this.name = name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.942052457+09:00", comments="Source field: users.created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.942102694+09:00", comments="Source field: users.created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}