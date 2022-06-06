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

package org.confab.videobridge.cc.config

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import java.time.Duration

class BandwidthProbingConfig private constructor() {
    /**
     * How often we check to send probing data
     */
    val paddingPeriodMs: Long by config {
        "org.confab.videobridge.PADDING_PERIOD_MS".from(ConfabConfig.legacyConfig)
        "videobridge.cc.padding-period"
            .from(ConfabConfig.newConfig).convertFrom<Duration> { it.toMillis() }
    }

    companion object {
        @JvmField
        val config = BandwidthProbingConfig()
    }
}
