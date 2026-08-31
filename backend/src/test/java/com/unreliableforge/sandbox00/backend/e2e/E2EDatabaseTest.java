package com.unreliableforge.sandbox00.backend.e2e;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("e2e")
public class E2EDatabaseTest {

    @Autowired
    Flyway flyway;

    @BeforeAll
    static void beforeAll() {
        // 何もしない（Testcontainers は TestcontainersConfiguration が起動）
    }

    @BeforeEach
    void setup() {
        // flyway.migrate(); // マイグレーション（ユニットテストと同じなので、ここでやらない）
        insertTestData(); // テストデータ投入
    }

    @Test
    void startBackendForE2E() {
        // Spring Boot を起動したまま待機
        // Playwrightのテストが終了したら、
        // backend:8080/actuator/shutdown
        // で停止。
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertTestData() {
        // テストデータ投入
    }
}
