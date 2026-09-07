package com.unreliableforge.sandbox00.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unreliableforge.sandbox00.backend.domain.extension.repository.UserProfileRepository;
import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;

/**
 * Profile情報を扱うクラス。
 * 自分自身の情報のみ扱う。
 * 
 * 認可処理でそのような制御を行うようにすること。とはいえ、認可をどうするかは戦線決めていない。
 * 
 */
@Service
public class ProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public Users getProfile(String sub) {

        return userProfileRepository.getUserProfile(sub);

    }

}
