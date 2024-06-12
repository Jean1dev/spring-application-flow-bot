package com.flowbot.application.shared;

import com.flowbot.application.context.TenantThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ApplicationScheduleService implements DefaultSchedule {
    private static final Logger log = LoggerFactory.getLogger(ApplicationScheduleService.class);
    private final ScheduledExecutorService scheduledExecutorService;

    public ApplicationScheduleService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        var currentTenant = TenantThreads.getTenantId();
        log.info("Scheduling task for tenant {}", currentTenant);

        scheduledExecutorService.schedule(() -> {
            TenantThreads.setTenantId(currentTenant);
            runnable.run();
        }, delay, unit);
    }
}
