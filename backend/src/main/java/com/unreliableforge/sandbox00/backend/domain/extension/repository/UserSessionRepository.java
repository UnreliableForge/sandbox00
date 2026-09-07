package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import com.unreliableforge.sandbox00.backend.domain.extension.entity.UserSessionEntity;

@Mapper
public interface UserSessionRepository {

    @SelectProvider(type = UserSessionRepositoryProvider.class)
    public Optional<UserSessionEntity> validSession(String id, LocalDateTime now);

    class UserSessionRepositoryProvider implements ProviderMethodResolver {

        public String validSession(String id, LocalDateTime now) {

            return """
                    select
                        sessions.user_sub as sub,
                        users.email as  email,
                        users.name as name
                        from sessions
                    join users on users.sub = sessions.user_sub
                    where
                        sessions.id = #{id}
                        and sessions.expires_at > #{now}
                    """;
        }
    }

}
