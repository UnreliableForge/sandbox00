package com.unreliableforge.sandbox00.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * application.properties を読み込むクラス(record)は、このパッケージにまとめておくこと。
 * @Value で直接プロパティを参照するのは推奨しない。
 * @Valueを使うと、ただでさえとっちらかるプロパティがよけいにややこしいことになる。
 */

/**
 * 
 * CognitoProperties
 * 
 * @param issuer
 * @param audience
 * @param url
 */

@ConfigurationProperties(prefix = "aws.cognito")
public record CognitoProperties(
        String issuer,
        String audience,
        String url) {
}