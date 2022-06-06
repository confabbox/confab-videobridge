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

package org.confab.videobridge.health.config

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import java.time.Duration

class HealthConfig private constructor() {
    val interval: Duration by config {
        "org.confab.videobridge.health.INTERVAL"
            .from(ConfabConfig.legacyConfig).convertFrom<Long>(Duration::ofMillis)
        "videobridge.health.interval".from(ConfabConfig.newConfig)
    }

    val timeout: Duration by config {
        "org.confab.videobridge.health.TIMEOUT"
            .from(ConfabConfig.legacyConfig).convertFrom<Long>(Duration::ofMillis)
        "videobridge.health.timeout".from(ConfabConfig.newConfig)
    }

    val maxCheckDuration: Duration by config("videobridge.health.max-check-duration".from(ConfabConfig.newConfig))

    val stickyFailures: Boolean by config {
        "org.confab.videobridge.health.STICKY_FAILURES".from(ConfabConfig.legacyConfig)
        "videobridge.health.sticky-failures".from(ConfabConfig.newConfig)
    }

    companion object {
        @JvmField
        val config = HealthConfig()
    }
}
