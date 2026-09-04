package com.unreliableforge.sandbox00.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unreliableforge.sandbox00.backend.constant.Severity;
import com.unreliableforge.sandbox00.backend.repository.records.ApiResponse;
import com.unreliableforge.sandbox00.backend.service.ProfileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class WhoamiController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/v1/whoami")
    public ApiResponse<?> whoami(HttpServletRequest request) {
        // セッションからユーザー情報を取得（フィルターですでに認証済み・存在確認済みの前提）
        HttpSession session = request.getSession(false);

        String id = session.getId();

        // ここでidから sesssionとかusersテーブルから値を取得する。
        // 既存のSessionsServideも使えると言えば使えるかもしれないが、別のServiceを作る。
        // また、ここでは自分の情報を操作するためのServiceとして、ProfileService という名前のServiceとする。
        // 他のユーザーを操作するためのServiceは、UserServiceとか、別な名前にする。

        // ここで渡すのは、SessionId ではなく、 subにしたい。
        // sessionから取得できるので、それを使う。
        // 本来は、
        // sessionId から sessions を検索し、sub を取得する。
        // 取得したsubでgetProfileを呼ぶ。
        // という手順となる。
        // MySQLだとちょっと重いような気がしなくもないので、このような実装にする場合、sessions管理にははRedisを使いましょう（認可情報もMySQLでないほうがいいと思う）。
        // 今はMySQLを使っていますが、Redisへの移行は難しくないでしょう。
        var profile = profileService.getProfile(id);

        // profileを直接bodyにしているけど、こんなことはやらないように。
        return new ApiResponse<>(Severity.SUCCESS, "Success", profile);
    }
}
