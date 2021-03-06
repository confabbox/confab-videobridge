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

package org.confab.videobridge

import org.confab.config.ConfabConfig.Companion.newConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import org.confab.metaconfig.optionalconfig

class VersionConfig private constructor() {
    val announceVersion: Boolean by config("videobridge.version.announce".from(newConfig))
    fun announceVersion() = announceVersion

    val release: String? by optionalconfig("videobridge.version.release".from(newConfig))

    companion object {
        @JvmField
        val config = VersionConfig()
    }
}
