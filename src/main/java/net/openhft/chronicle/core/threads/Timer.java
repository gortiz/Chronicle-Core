/*
 * Copyright 2016-2020 chronicle.software
 *
 *       https://chronicle.software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.openhft.chronicle.core.threads;

import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;
import org.jetbrains.annotations.NotNull;

public class Timer {

    @NotNull
    private final CancellableTimer cancellableTimer;

    /**
     * @param eventLoop the event loop that the timer task is run on
     */
    public Timer(@NotNull EventLoop eventLoop) {
        this(eventLoop, SystemTimeProvider.INSTANCE);
    }

    public Timer(@NotNull EventLoop eventLoop, @NotNull TimeProvider timeProvider) {
        this.cancellableTimer = new CancellableTimer(eventLoop, timeProvider);
    }

    /**
     * uses the event loop thread to call the event handler periodically, the time that the event is
     * called back is best-effort, but if the thread is busy that call back maybe delayed
     *
     * @param eventHandler   the handler to be called back
     * @param initialDelayMs how long in milliseconds to wait before being called back
     * @param periodMs       the poll interval of being called
     */
    public void scheduleAtFixedRate(@NotNull VanillaEventHandler eventHandler,
                                    long initialDelayMs,
                                    long periodMs) {
        cancellableTimer.scheduleAtFixedRate(eventHandler, initialDelayMs, periodMs);
    }

    /**
     * uses the event loop thread to call the event handler periodically, the time that the event is
     * called back is best-effort, but if the thread is busy that call back maybe delayed
     *
     * @param eventHandler   the handler to be called back
     * @param initialDelayMs how long in milliseconds to wait before being called back
     * @param periodMs       the poll interval of being called
     * @param priority       the priority of the event handler
     */
    public void scheduleAtFixedRate(@NotNull VanillaEventHandler eventHandler,
                                    long initialDelayMs,
                                    long periodMs,
                                    HandlerPriority priority) {
        cancellableTimer.scheduleAtFixedRate(eventHandler, initialDelayMs, periodMs, priority);
    }

    /**
     * Schedule a handler to run once after a delay
     *
     * @param eventHandler   the handler to be called back
     * @param initialDelayMs how long in milliseconds to wait before being called back
     */
    public void schedule(@NotNull Runnable eventHandler, long initialDelayMs) {
        cancellableTimer.schedule(eventHandler, initialDelayMs);
    }
}

