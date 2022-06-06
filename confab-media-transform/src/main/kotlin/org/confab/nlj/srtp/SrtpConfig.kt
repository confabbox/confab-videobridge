/*
 * Copyright @ 2019 - present 8x8, Inc.
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
package org.confab.nlj.srtp

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.optionalconfig
import org.confab.nlj.srtp.SrtpUtil.Companion.getSrtpProtectionProfileFromName

class SrtpConfig {
    companion object {
        val maxConsecutivePacketsDiscardedEarly: Int by config {
            "jmt.srtp.max-consecutive-packets-discarded-early".from(ConfabConfig.newConfig)
        }

        val protectionProfiles: List<Int> by config {
            "jmt.srtp.protection-profiles".from(ConfabConfig.newConfig)
                .convertFrom<List<String>> { list -> list.map { getSrtpProtectionProfileFromName(it) } }
        }

        val factoryClass: String? by optionalconfig {
            "jmt.srtp.factory-class".from(ConfabConfig.newConfig)
        }
    }
}
