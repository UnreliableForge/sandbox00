package com.unreliableforge.sandbox00.backend.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

public interface SessionService {
    String createSession(String sub);

    String getSub(String sessionId);

    void deleteSession(String sessionId);
}

/**
 * ローカル用
 * AWS用ではDBに保存するようになります。
 */
@Service
@Profile("local")
class LocalSessionService implements SessionService {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public String createSession(String sub) {
        String sessionId = UUID.randomUUID().toString();
        store.put(sessionId, sub);
        return sessionId;
    }

    public String getSub(String sessionId) {
        return store.get(sessionId);
    }

    public void deleteSession(String sessionId) {
        store.remove(sessionId);
    }
}

/*
 * AWS上で使用するSessionService.
 * 現在はlocalと同じですが、実際にはDBに登録する処理にします。
 */
@Service
@Profile("aws")
class AwsLocalSessionService implements SessionService {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public String createSession(String sub) {
        String sessionId = UUID.randomUUID().toString();
        store.put(sessionId, sub);
        return sessionId;
    }

    public String getSub(String sessionId) {
        return store.get(sessionId);
    }

    public void deleteSession(String sessionId) {
        store.remove(sessionId);
    }
}

/*
 * E2Eテスト用SessionService.
 * このProfileは、E2Eテストで有効になります。
 * 現在はlocalと同じですが、E2Eテスト用の処理に置き換えます。
 */
@Service
@Profile("e2e")
class E2ESessionService implements SessionService {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public String createSession(String sub) {
        String sessionId = UUID.randomUUID().toString();
        store.put(sessionId, sub);
        return sessionId;
    }

    public String getSub(String sessionId) {
        return store.get(sessionId);
    }

    public void deleteSession(String sessionId) {
        store.remove(sessionId);
    }
}
