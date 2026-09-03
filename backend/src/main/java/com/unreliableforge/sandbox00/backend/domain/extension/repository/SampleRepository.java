package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;

/*
 * 他のMapperを使用し、 Repository内で処理をはさむ場合のサンプル。
 */
@Mapper
public class SampleRepository {

    @Autowired
    private SampleProvidedBasedRepository sampleProvidedBasedRepository;

    public Optional<Users> validSession(String userId) {
        /*
         * Repository内で直接Mapperを実装しない場合のサンプル。
         * 以下のような要件がある場合に使用する。
         * - 複数 mapper をまとめて 1 つの操作にしたい
         * - トランザクション境界を Repository に置きたい
         * - 複雑なビジネスロジックを Service に置きたくない
         * - 返却型を変換したい（DTO → Domain）
         * - キャッシュやロギングを挟みたい
         * - 再生成の影響をRepositoryで吸収したい
         */
        return sampleProvidedBasedRepository.validSession(userId);
    }
}
