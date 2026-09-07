package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;
import com.unreliableforge.sandbox00.backend.domain.generated.mapper.UsersMapper;

@Component
public class UserProfileRepository {

    @Autowired
    private UsersMapper usersMapper;

    public Users getUserProfile(String sub) {

        Users users = usersMapper.selectByPrimaryKey(sub);

        return users;

    }

}
