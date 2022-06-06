/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.confab.videobridge

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import java.time.Duration

class EndpointConnectionStatusConfig private constructor() {
    val firstTransferTimeout: Duration by config {
        "org.confab.videobridge.EndpointConnectionStatus.FIRST_TRANSFER_TIMEOUT"
            .from(ConfabConfig.legacyConfig)
            .convertFrom<Long>(Duration::ofMillis)
        "videobridge.ep-connection-status.first-transfer-timeout".from(ConfabConfig.newConfig)
    }

    val maxInactivityLimit: Duration by config {
        "org.confab.videobridge.EndpointConnectionStatus.MAX_INACTIVITY_LIMIT"
            .from(ConfabConfig.legacyConfig)
            .convertFrom<Long>(Duration::ofMillis)
        "videobridge.ep-connection-status.max-inactivity-limit".from(ConfabConfig.newConfig)
    }

    val intervalMs: Long by config {
        "videobridge.ep-connection-status.check-interval".from(ConfabConfig.newConfig)
            .convertFrom<Duration>(Duration::toMillis)
    }

    companion object {
        @JvmField
        val config = EndpointConnectionStatusConfig()
    }
}
