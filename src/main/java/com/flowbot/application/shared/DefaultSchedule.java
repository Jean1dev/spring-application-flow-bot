package com.flowbot.application.shared;

import java.util.concurrent.TimeUnit;

public interface DefaultSchedule {

    void schedule(Runnable runnable, long delay, TimeUnit unit);
}
