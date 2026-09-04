package com.unreliableforge.sandbox00.backend.handler;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.unreliableforge.sandbox00.backend.constant.Severity;
import com.unreliableforge.sandbox00.backend.repository.records.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ResponseStatus例外ハンドラ。
    // この例外は、SpringBootがthrowするものでなく、自分でthrowする。
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex,
            HttpServletRequest request) {
        String errorId = "E!" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.warn("[{}] ステータス例外発生: {}", errorId, ex.getReason(), ex.getMessage());

        ApiResponse<Void> responseBody = new ApiResponse<>(Severity.ERROR, ex.getReason() + "（エラーID: " + errorId + "）");

        return ResponseEntity
                .status(ex.getStatusCode()) // 投げられたステータスコード（401や403など）をそのまま使う
                .body(responseBody);
    }

    // 未認可（403）のキャッチ
    // SpringのAuthenticationによる例外。
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex,
            HttpServletRequest request) {
        String errorId = "E!" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.warn("[{}] アクセス拒否（権限不足）: {}", errorId, ex.getMessage());

        ApiResponse<Void> responseBody = new ApiResponse<>(Severity.ERROR, "権限がありません（エラーID: " + errorId + "）");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(responseBody);
    }

    // 予期せぬすべての例外（Exception）をキャッチして500を返す
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, HttpServletRequest request) {

        // 例外時にエラーIDを生成します。
        // このエラーIDは、 ログと画面（厳密にいえば、画面ではなくレスポンス）の両方に同じ値を出力します。
        // これにより、画面に表示されるエラーIDでログを検索することで、場所を特定しやすくなります。
        // エラーコードなんて表示しても、それだけではぜんぜんわからない。結局はログを見ることになるので、このようにエラーから場所を特定できるようにしてみました。
        // UUIDの先頭8桁しか使用していないので、もしかすると、同じIDが生成されることもあるかもしれません。
        // 数字とアルファベット大文字で8文字なので、可能性は限りなく低いし、同じIDが生成されてもちょっと検索がめんどくさくなるくらいで、大した問題にはならないでしょう。
        // 8文字ぜんぶわからくても、たぶん4文字くらいわかればじゅうぶんに探せると思うし。
        String errorId = "E!" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.error("[{}] 予期しない例外が発生しました。{}", errorId, ex.getMessage(), ex);

        ApiResponse<Void> responseBody = new ApiResponse<Void>(Severity.ERROR, errorId);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBody);
    }
}