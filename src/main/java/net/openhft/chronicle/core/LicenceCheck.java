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

package net.openhft.chronicle.core;

import net.openhft.chronicle.core.io.IOTools;

import javax.naming.TimeLimitExceededException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.BiConsumer;

import static net.openhft.chronicle.core.Jvm.startup;
import static net.openhft.chronicle.core.Jvm.warn;

public interface LicenceCheck {

    String CHRONICLE_LICENSE = "chronicle.license";

    static void check(String product, Class<?> caller) {
        final BiConsumer<Long, String> logLicenseExpiryDetails = (days, owner) -> {
            String ownerId = owner == null ? "" : "for " + owner + " ";
            String expires = "The license " + ownerId + "expires";
            String message = days <= 1 ? expires + " in 1 day" : expires + " in " + days + " days";

            if (days > 500)
                message = expires + " in about " + (days / 365) + " years";

            String logMessage = message + ". At which point, this product will stop working, if you wish to renew this licence please contact sales@chronicle.software";
            if (days < 30)
                warn().on(LicenceCheck.class, logMessage);
            else
                startup().on(LicenceCheck.class, logMessage);
        };

        String key = Jvm.getProperty(CHRONICLE_LICENSE); // make sure this was loaded first.
        if (key == null || !key.contains(product + '.')) {
            String expiryDateFile = product + ".expiry-date";
            try {
                String source = new String(IOTools.readFile(LicenceCheck.class, expiryDateFile));
                Date expriyDate = new SimpleDateFormat("yyyy-MM-dd").parse(source);
                long days = (expriyDate.getTime() - System.currentTimeMillis()) / 86400000;
                if (days < 0)
                    throw Jvm.rethrow(new TimeLimitExceededException("Failed to read '" + expiryDateFile));
                logLicenseExpiryDetails.accept(days, null);
            } catch (Throwable t) {
                throw Jvm.rethrow(new TimeLimitExceededException("Failed to read expiry date, from '" + expiryDateFile + "'"));
            }
        } else {
            int start = key.indexOf("expires=") + 8;
            int end = key.indexOf(",", start);
            LocalDate date = LocalDate.parse(key.substring(start, end));
            int start2 = key.indexOf("owner=") + 6;
            int end2 = key.indexOf(",", start2);
            String owner = key.substring(start2, end2);
            long days = date.toEpochDay() - System.currentTimeMillis() / 86400000;
            if (days < 0)
                throw Jvm.rethrow(new TimeLimitExceededException());
            logLicenseExpiryDetails.accept(days, owner);
        }
    }

    /**
     * checks if the function you are about to call is part of an enterprise product, if the licence
     * fails a runtime exception will be thrown
     */
    void licenceCheck();

    boolean isAvailable();

}
