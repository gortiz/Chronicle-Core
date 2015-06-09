/*
 * Copyright 2015 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.core;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class OSTest {
    @Test
    public void testIs64Bit() {
        System.out.println("is64 = " + OS.is64Bit());
    }

    @Test
    public void testGetProcessId() {
        System.out.println("pid = " + OS.getProcessId());
    }

    @Test
    @Ignore("Should always pass, or crash the JVM based on length")
    public void testMap() throws Exception {
        if (!OS.isWindows()) return;

        // crashes the JVM.
//        long length = (4L << 30L) + (64 << 10);
        // doesn't crash the JVM.
        long length = (4L << 30L);

        String name = OS.TMP + "/deleteme";
        new File(name).deleteOnExit();
        FileChannel fc = new RandomAccessFile(name, "rw").getChannel();
        long address = OS.map0(fc, OS.imodeFor(FileChannel.MapMode.READ_WRITE), 0, length);
        for (long offset = 0; offset < length; offset += OS.pageSize())
            OS.memory().writeLong(address + offset, offset);
        OS.unmap(address, length);
    }
}