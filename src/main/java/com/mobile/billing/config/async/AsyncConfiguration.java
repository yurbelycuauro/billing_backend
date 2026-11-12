package com.mobile.billing.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mobile.billing.domain.ports.out.TenantContextPort;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "consumptionTaskExecutor")
    public ThreadPoolTaskExecutor consumptionTaskExecutor(TenantContextPort tenantContextPort) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("consumption-");
        executor.setTaskDecorator(new TenantContextTaskDecorator(tenantContextPort));
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
