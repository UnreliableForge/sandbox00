package com.unreliableforge.sandbox00.backend.domain.generated.mapper;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;
import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

public interface UsersMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.946492823+09:00", comments="Source Table: users")
    @Delete({
        "delete from users",
        "where sub = #{sub,jdbcType=CHAR}"
    })
    int deleteByPrimaryKey(String sub);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.949060611+09:00", comments="Source Table: users")
    @Insert({
        "insert into users (sub, email, ",
        "name, created_at)",
        "values (#{sub,jdbcType=CHAR}, #{email,jdbcType=VARCHAR}, ",
        "#{name,jdbcType=VARCHAR}, #{createdAt,jdbcType=TIMESTAMP})"
    })
    int insert(Users row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.950548699+09:00", comments="Source Table: users")
    @Select({
        "select",
        "sub, email, name, created_at",
        "from users",
        "where sub = #{sub,jdbcType=CHAR}"
    })
    @Results({
        @Result(column="sub", property="sub", jdbcType=JdbcType.CHAR, id=true),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    Users selectByPrimaryKey(String sub);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.954492748+09:00", comments="Source Table: users")
    @Select({
        "select",
        "sub, email, name, created_at",
        "from users"
    })
    @Results({
        @Result(column="sub", property="sub", jdbcType=JdbcType.CHAR, id=true),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Users> selectAll();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2026-09-03T21:11:57.955845024+09:00", comments="Source Table: users")
    @Update({
        "update users",
        "set email = #{email,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=VARCHAR},",
          "created_at = #{createdAt,jdbcType=TIMESTAMP}",
        "where sub = #{sub,jdbcType=CHAR}"
    })
    int updateByPrimaryKey(Users row);
}