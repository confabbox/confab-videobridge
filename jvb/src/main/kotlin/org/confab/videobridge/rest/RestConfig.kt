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

package org.confab.videobridge.rest

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.videobridge.Videobridge

class RestConfig private constructor() {
    private val colibriRestEnabled: Boolean by config {
        // If the value was passed via a command line arg, we set it as a system
        // variable at this path, which the new config will pick up
        Videobridge.REST_API_PNAME.from(ConfabConfig.newConfig)
        "videobridge.apis.rest.enabled".from(ConfabConfig.newConfig)
    }
    private val legacyColibriRestEnabled: Boolean by config {
        "org.confab.videobridge.ENABLE_REST_COLIBRI".from(ConfabConfig.legacyConfig)
        "default" { true }
    }

    /**
     * Due to historical reasons the COLIBRI REST API is controlled in multiple ways. There is:
     * 1. A command line argument (--apis=rest)
     * 2. A new config property (videobridge.apis.rest.enabled)
     * 3. A legacy config property ENABLE_REST_COLIBRI
     *
     * The last one defaults to `true` so it is simply an old way to force the API to be disabled. It is implemented
     * in [legacyColibriRestEnabled]. The first two are implemented in [colibriRestEnabled], and this property
     * implements the combined logic.
     */
    private val colibriEnabled
        get() = colibriRestEnabled && legacyColibriRestEnabled

    /**
     * The debug API (query conference state, enable/disable debug features). It currently defaults to being enabled,
     * but that might change in the future.
     */
    private val debugEnabled: Boolean by config {
        "videobridge.rest.debug.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * The health-check API.
     */
    private val healthEnabled: Boolean by config {
        "videobridge.rest.health.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * The property which enables/disables the graceful shutdown API.
     */
    private val shutdownEnabledProperty: Boolean by config {
        "org.confab.videobridge.ENABLE_REST_SHUTDOWN".from(ConfabConfig.legacyConfig)
        "videobridge.rest.shutdown.enabled".from(ConfabConfig.newConfig)
    }
    /**
     * Due to historical reasons the shutdown API is only enabled when the COLIBRI API is enabled.
     */
    private val shutdownEnabled
        get() = colibriEnabled && shutdownEnabledProperty

    /**
     * The drain API.
     */
    private val drainEnabled: Boolean by config {
        "videobridge.rest.drain.enabled".from(ConfabConfig.newConfig)
    }

    private val versionEnabled: Boolean by config {
        "videobridge.rest.version.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * Whether any of the REST APIs are enabled by the configuration. If there aren't, the HTTP server doesn't need to
     * be started at all.
     */
    fun isEnabled() = colibriEnabled || debugEnabled || healthEnabled ||
        shutdownEnabled || drainEnabled || versionEnabled

    fun isEnabled(api: RestApis) = when (api) {
        RestApis.COLIBRI -> colibriEnabled
        RestApis.DEBUG -> debugEnabled
        RestApis.HEALTH -> healthEnabled
        RestApis.SHUTDOWN -> shutdownEnabled
        RestApis.DRAIN -> drainEnabled
        RestApis.VERSION -> versionEnabled
    }

    companion object {
        @JvmField
        val config = RestConfig()
    }
}
