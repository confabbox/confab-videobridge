/*
 * Copyright @ 2020 - present 8x8, Inc.
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

package org.confab.videobridge.sctp

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config

class SctpConfig private constructor() {
    val enabled: Boolean by config { "videobridge.sctp.enabled".from(ConfabConfig.newConfig) }

    fun enabled() = enabled

    companion object {
        @JvmField
        val config = SctpConfig()
    }
}
