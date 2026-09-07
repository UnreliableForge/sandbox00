package com.unreliableforge.sandbox00.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/v1/whoami")
    public ApiResponse<?> whoami(HttpServletRequest request) {
        // セッションからユーザー情報を取得（フィルターですでに認証済み・存在確認済みの前提）
        HttpSession session = request.getSession(false);

        var springSecurityContext = (SecurityContext) session
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        String sub = springSecurityContext.getAuthentication().getPrincipal().toString();

        // String id = session.getId();

        // ここでidから sesssionとかusersテーブルから値を取得する。
        // 既存のSessionsServideも使えると言えば使えるかもしれないが、別のServiceを作る。
        // また、ここでは自分の情報を操作するためのServiceとして、ProfileService という名前のServiceとする。
        // 他のユーザーを操作するためのServiceは、UserServiceとか、別な名前にする。

        // ここで渡すのは、SessionId ではなく、 subにする。sessionから取得できるので、それを使う。
        var profile = profileService.getProfile(sub);

        // profileを直接bodyにしているけど、こんなことはやらない方がいいと思います。
        return new ApiResponse<>(Severity.SUCCESS, "Success", profile);
    }
}
