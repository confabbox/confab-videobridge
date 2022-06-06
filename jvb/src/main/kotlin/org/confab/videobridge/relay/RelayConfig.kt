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

package org.confab.videobridge.relay

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.optionalconfig

class RelayConfig private constructor() {
    val enabled: Boolean by config {
        "videobridge.octo.enabled".from(ConfabConfig.newConfig)
        "videobridge.relay.enabled".from(ConfabConfig.newConfig)
    }

    val region: String? by optionalconfig {
        "org.confab.videobridge.REGION".from(ConfabConfig.legacyConfig)
        "videobridge.octo.region".from(ConfabConfig.newConfig)
        "videobridge.relay.region".from(ConfabConfig.newConfig)
    }

    val relayId: String by config {
        "videobridge.octo.relay-id".from(ConfabConfig.newConfig)
        "videobridge.relay.relay-id".from(ConfabConfig.newConfig)
    }

    companion object {
        @JvmField
        val config = RelayConfig()
    }
}
