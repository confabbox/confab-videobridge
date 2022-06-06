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

package org.confab.videobridge.websocket.config

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.optionalconfig

class WebsocketServiceConfig private constructor() {
    /**
     * Whether [org.confab.videobridge.websocket.ColibriWebSocketService] is enabled
     */
    val enabled: Boolean by config {
        // The old property is named 'disable', while the new one
        // is 'enable', so invert the old value
        "org.confab.videobridge.rest.COLIBRI_WS_DISABLE"
            .from(ConfabConfig.legacyConfig).transformedBy { !it }
        "videobridge.websockets.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * The domain name used in URLs advertised for COLIBRI WebSockets.
     */
    val domain: String by config {
        onlyIf("Websockets are enabled", ::enabled) {
            "org.confab.videobridge.rest.COLIBRI_WS_DOMAIN".from(ConfabConfig.legacyConfig)
            "videobridge.websockets.domain".from(ConfabConfig.newConfig)
        }
    }

    /**
     * Whether the "wss" or "ws" protocol should be used for websockets
     */
    val useTls: Boolean? by optionalconfig {
        onlyIf("Websockets are enabled", ::enabled) {
            "org.confab.videobridge.rest.COLIBRI_WS_TLS".from(ConfabConfig.legacyConfig)
            "videobridge.websockets.tls".from(ConfabConfig.newConfig)
        }
    }

    /**
     * Whether compression (permessage-deflate) should be used for websockets
     */
    private val enableCompression: Boolean by config {
        onlyIf("Websockets are enabled", ::enabled) {
            "videobridge.websockets.enable-compression".from(ConfabConfig.newConfig)
        }
    }

    fun shouldEnableCompression() = enableCompression

    /**
     * The server ID used in URLs advertised for COLIBRI WebSockets.
     */
    val serverId: String by config {
        onlyIf("Websockets are enabled", ::enabled) {
            "org.confab.videobridge.rest.COLIBRI_WS_SERVER_ID".from(ConfabConfig.legacyConfig)
            "videobridge.websockets.server-id".from(ConfabConfig.newConfig)
        }
    }

    companion object {
        @JvmField
        val config = WebsocketServiceConfig()
    }
}
