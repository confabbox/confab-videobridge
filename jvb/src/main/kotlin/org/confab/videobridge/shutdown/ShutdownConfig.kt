/*
 * Copyright @ 2022 - Present, 8x8 Inc
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

package org.confab.videobridge.shutdown

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import java.time.Duration

class ShutdownConfig {
    private constructor()

    val gracefulShutdownMaxDuration: Duration by config {
        "videobridge.shutdown.graceful-shutdown-max-duration".from(ConfabConfig.newConfig)
    }
    val gracefulShutdownMinParticipants: Int by config {
        "videobridge.shutdown.graceful-shutdown-min-participants".from(ConfabConfig.newConfig)
    }
    val shuttingDownDelay: Duration by config {
        "videobridge.shutdown.shutting-down-delay".from(ConfabConfig.newConfig)
    }

    companion object {
        @JvmField
        val config = ShutdownConfig()
    }
}
