package com.unreliableforge.sandbox00.backend.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 外部から停止するactuator.
 * 本番ではactuatorごと無効にすること。
 * 
 */
@Component
@Endpoint(id = "shutdown")
public class ShutdownEndpoint {

    @Autowired
    private ApplicationContext context;

    @WriteOperation
    public String shutdown() {
        Thread thread = new Thread(() -> {
            SpringApplication.exit(context, () -> 0);
        });
        thread.start();
        return "Shutting down";
    }
}
