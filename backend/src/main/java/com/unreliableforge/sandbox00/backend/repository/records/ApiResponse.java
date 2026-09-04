package com.unreliableforge.sandbox00.backend.repository.records;

import java.util.List;

import com.unreliableforge.sandbox00.backend.constant.Severity;

/**
 * レスポンスのベース。
 * severityとmessageのみのレスポンスはこれを直接使用する。
 * 使用例。
 * new BaseResponse<Void>( Severity.OK, "OK");
 * 
 * @param severity
 * @param message
 * @param data
 * @param invalid
 */
public record ApiResponse<T>(
        Severity severity,
        String message,
        T data,
        List<ApiInvalid> invalid) {

    public ApiResponse(Severity severity, String message) {
        this(severity, message, null, List.of());
    }

    public ApiResponse(Severity severity, String message, T data) {
        this(severity, message, data, List.of());
    }

    public ApiResponse(Severity severity, String message, List<ApiInvalid> invalid) {
        this(severity, message, null, invalid);
    }

}
