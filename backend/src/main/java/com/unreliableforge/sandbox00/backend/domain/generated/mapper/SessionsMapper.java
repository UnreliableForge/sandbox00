package com.unreliableforge.sandbox00.backend.domain.generated.mapper;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Sessions;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

public interface SessionsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.959854179+09:00", comments="Source Table: sessions")
    @Delete({
        "delete from sessions",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.960062743+09:00", comments="Source Table: sessions")
    @Insert({
        "insert into sessions (id, user_sub, ",
        "created_at, expires_at)",
        "values (#{id,jdbcType=VARCHAR}, #{userSub,jdbcType=CHAR}, ",
        "#{createdAt,jdbcType=TIMESTAMP}, #{expiresAt,jdbcType=TIMESTAMP})"
    })
    int insert(Sessions row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.960295255+09:00", comments="Source Table: sessions")
    @Select({
        "select",
        "id, user_sub, created_at, expires_at",
        "from sessions",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="user_sub", property="userSub", jdbcType=JdbcType.CHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP)
    })
    Sessions selectByPrimaryKey(String id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.960659552+09:00", comments="Source Table: sessions")
    @Select({
        "select",
        "id, user_sub, created_at, expires_at",
        "from sessions"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="user_sub", property="userSub", jdbcType=JdbcType.CHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Sessions> selectAll();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.960931098+09:00", comments="Source Table: sessions")
    @Update({
        "update sessions",
        "set user_sub = #{userSub,jdbcType=CHAR},",
          "created_at = #{createdAt,jdbcType=TIMESTAMP},",
          "expires_at = #{expiresAt,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(Sessions row);
}