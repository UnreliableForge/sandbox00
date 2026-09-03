package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;

/*
 * Repositiryに直接Mapperを実装してしまう場合のサンプル。
 */
@Mapper
public interface SampleProvidedBasedRepository {

    @SelectProvider(type = SampleProvider.class)
    public Optional<Users> validSession(String userId);

    class SampleProvider implements ProviderMethodResolver {

        public String validSession(String userId) {
            // SQLビルダーを使ってSQLを組み立てる。
            // return new SQL() {
            // {
            // SELECT("todo_id", "todo_title", "finished", "created_at");
            // FROM("todo");
            // WHERE("todo_id = #{userId}");
            // }
            // }.toString();
            // もしくは、SQLを文字列で返す。
            return "SELECT ...  WHERE id = #{userId}";
        }

    }
}
