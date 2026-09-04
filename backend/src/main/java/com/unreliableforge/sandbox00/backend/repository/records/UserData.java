package com.unreliableforge.sandbox00.backend.repository.records;

/**
 * レスポンスのデータ部分のサンプル。
 * といっても、ただのrecordです。
 * ApiResponseのところにこの型を指定します。
 * new ApiResponse<UserData>( Severity.SUCCESS, "success", new UserData("sub",
 * "mail", "name" ));
 * 
 * @param sub
 * @param mail
 * @param name
 */

public record UserData(
        String sub,
        String mail,
        String name) {

}
