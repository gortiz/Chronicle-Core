/*
 * Copyright 2016-2022 chronicle.software
 *
 *       https://chronicle.software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.core.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UniqueMicroTimeProviderTest {

    @Test
    public void currentTimeMicros() throws IllegalStateException {
        UniqueMicroTimeProvider tp = new UniqueMicroTimeProvider();
        SetTimeProvider stp = new SetTimeProvider(SystemTimeProvider.INSTANCE.currentTimeNanos());
        tp.provider(stp);
        long last = 0;
        for (int i = 0; i < 4_000; i++) {
            stp.advanceNanos(i);
            long time = tp.currentTimeMicros();
            assertEquals(LongTime.toMicros(time), time);
            assertTrue(time > last);
            last = time;
        }
    }

    @Test
    public void currentTimeNanos() throws IllegalStateException {
        UniqueMicroTimeProvider tp = new UniqueMicroTimeProvider();
        SetTimeProvider stp = new SetTimeProvider(SystemTimeProvider.INSTANCE.currentTimeNanos());
        tp.provider(stp);
        long last = 0;
        for (int i = 0; i < 4_000; i++) {
            stp.advanceNanos(i);
            long time = tp.currentTimeNanos();
            assertEquals(LongTime.toNanos(time), time);
            assertTrue(time / 1000 > last);
            last = time / 1000;
        }
    }
}