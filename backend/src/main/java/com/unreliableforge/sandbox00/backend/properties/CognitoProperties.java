package com.unreliableforge.sandbox00.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * プロパティファイルの値を使用する場合、このようなクラスを作成すること。
 * 直接 @Value を使うことは推奨しない。
 * 
 * 単純なENVとかならいいかもしれないけど、コード内でわざわざENVを見ることなんてないよね。
 */

@ConfigurationProperties(prefix = "aws.cognito")
public record CognitoProperties(
        String issuer,
        String audience,
        String url) {
}
