package com.unreliableforge.sandbox00.backend.service;

import org.springframework.stereotype.Service;

import com.unreliableforge.sandbox00.backend.domain.extension.entity.UserProfileEntity;

/**
 * Profile情報を扱うクラス。
 * 自分自身の情報のみ扱う。
 * 
 * 認可処理でそのような制御を行うようにすること。とはいえ、認可をどうするかは戦線決めていない。
 * 
 */
@Service
public class ProfileService {

    public UserProfileEntity getProfile(String sub) {
        return null;
    }

}
