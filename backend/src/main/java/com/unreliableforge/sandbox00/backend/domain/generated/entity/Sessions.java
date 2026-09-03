package com.unreliableforge.sandbox00.backend.domain.generated.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

public class Sessions {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.95847182+09:00", comments="Source field: sessions.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959057142+09:00", comments="Source field: sessions.user_sub")
    private String userSub;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959211531+09:00", comments="Source field: sessions.created_at")
    private LocalDateTime createdAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.95944244+09:00", comments="Source field: sessions.expires_at")
    private LocalDateTime expiresAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.958756218+09:00", comments="Source field: sessions.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.958989501+09:00", comments="Source field: sessions.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959112469+09:00", comments="Source field: sessions.user_sub")
    public String getUserSub() {
        return userSub;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959167175+09:00", comments="Source field: sessions.user_sub")
    public void setUserSub(String userSub) {
        this.userSub = userSub;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959332186+09:00", comments="Source field: sessions.created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959394016+09:00", comments="Source field: sessions.created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959498569+09:00", comments="Source field: sessions.expires_at")
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959558956+09:00", comments="Source field: sessions.expires_at")
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}