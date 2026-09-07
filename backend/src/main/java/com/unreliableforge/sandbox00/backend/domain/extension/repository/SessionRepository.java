package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Sessions;
import com.unreliableforge.sandbox00.backend.domain.generated.mapper.SessionsMapper;
import com.unreliableforge.sandbox00.backend.domain.generated.mapper.UsersMapper;

/**
 * 
 * SessionRepository
 */
@Component
public class SessionRepository {

    @Autowired
    private SessionsMapper sessionsMapper;

    @Autowired
    private UsersMapper usersMapper;

    /**
     * sessions テーブルにセッションレコードを作成。
     * 
     * @param id       セッションID
     * @param sub      ユーザーの識別子。Cognito の sub
     * @param expireAt 有効期限
     * @return 成功または失敗。
     */
    public boolean createSession(String id, String sub, LocalDateTime expireAt) {

        Sessions sessions = new Sessions();

        sessions.setId(id);
        sessions.setUserSub(sub);
        sessions.setCreatedAt(LocalDateTime.now());
        sessions.setExpiresAt(expireAt);

        int resullt = sessionsMapper.insert(sessions);

        return resullt == 1;
    }

    public boolean isUserExists(String sub) {

        return !(usersMapper.selectByPrimaryKey(sub) == null);

    }

    /**
     * セッション情報を削除する。
     * 
     * @param id セッションID
     */
    public void invalidateSession(String id) {
        sessionsMapper.deleteByPrimaryKey(id);
    }

}
