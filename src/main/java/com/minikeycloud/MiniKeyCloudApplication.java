package com.minikeycloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-15 20:49:37
 */
@SpringBootApplication(scanBasePackages = {"com.minikeycloud", "com.easypan"})
// 扫描mapper
@MapperScan(basePackages = {"com.minikeycloud.mappers", "com.easypan.mappers"})
// 开启事务
@EnableTransactionManagement
// 开启定时任务
@EnableScheduling
// 开启异步任务
@EnableAsync
public class MiniKeyCloudApplication
{
    public static void main(String[] args) {
        SpringApplication.run(MiniKeyCloudApplication.class, args);
    }
}


