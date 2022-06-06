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

package org.confab.videobridge.ice

import org.ice4j.ice.KeepAliveStrategy
import org.ice4j.ice.NominationStrategy
import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import org.confab.metaconfig.optionalconfig

class IceConfig private constructor() {
    /**
     * Is ICE/TCP enabled.
     */
    val tcpEnabled: Boolean by config {
        // The old property is named 'disable', while the new one
        // is 'enable', so invert the old value
        "org.confab.videobridge.DISABLE_TCP_HARVESTER".from(ConfabConfig.legacyConfig).transformedBy { !it }
        "videobridge.ice.tcp.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * The ICE/TCP port.
     */
    val tcpPort: Int by config {
        "org.confab.videobridge.TCP_HARVESTER_PORT".from(ConfabConfig.legacyConfig)
        "videobridge.ice.tcp.port".from(ConfabConfig.newConfig)
    }

    /**
     * The additional port to advertise, or null if none is configured.
     */
    val tcpMappedPort: Int? by optionalconfig {
        "org.confab.videobridge.TCP_HARVESTER_MAPPED_PORT".from(ConfabConfig.legacyConfig)
        "videobridge.ice.tcp.mapped-port".from(ConfabConfig.newConfig)
    }

    /**
     * Whether ICE/TCP should use "ssltcp" or not.
     */
    val iceSslTcp: Boolean by config {
        "org.confab.videobridge.TCP_HARVESTER_SSLTCP".from(ConfabConfig.legacyConfig)
        "videobridge.ice.tcp.ssltcp".from(ConfabConfig.newConfig)
    }

    /**
     * The ICE UDP port.
     */
    val port: Int by config {
        "org.confab.videobridge.SINGLE_PORT_HARVESTER_PORT".from(ConfabConfig.legacyConfig)
        "videobridge.ice.udp.port".from(ConfabConfig.newConfig)
    }

    /**
     * The prefix to STUN username fragments we generate.
     */
    val ufragPrefix: String? by optionalconfig {
        "org.confab.videobridge.ICE_UFRAG_PREFIX".from(ConfabConfig.legacyConfig)
        "videobridge.ice.ufrag-prefix".from(ConfabConfig.newConfig)
    }

    val keepAliveStrategy: KeepAliveStrategy by config {
        "org.confab.videobridge.KEEP_ALIVE_STRATEGY"
            .from(ConfabConfig.legacyConfig)
            .convertFrom<String> { KeepAliveStrategy.fromString(it) }
        "videobridge.ice.keep-alive-strategy"
            .from(ConfabConfig.newConfig)
            .convertFrom<String> { KeepAliveStrategy.fromString(it) }
    }

    /**
     * Whether the ice4j "component socket" mode is used.
     */
    val useComponentSocket: Boolean by config {
        "org.confab.videobridge.USE_COMPONENT_SOCKET".from(ConfabConfig.legacyConfig)
        "videobridge.ice.use-component-socket".from(ConfabConfig.newConfig)
    }

    val resolveRemoteCandidates: Boolean by config(
        "videobridge.ice.resolve-remote-candidates".from(ConfabConfig.newConfig)
    )

    /**
     * The ice4j nomination strategy policy.
     */
    val nominationStrategy: NominationStrategy by config {
        "videobridge.ice.nomination-strategy"
            .from(ConfabConfig.newConfig)
            .convertFrom<String> { NominationStrategy.fromString(it) }
    }

    /**
     * Whether to advertise ICE candidates with private IP addresses (RFC1918 IPv4 addresses and
     * fec0::/10 or fc00::/7 IPv6 addresses).
     */
    val advertisePrivateCandidates: Boolean by config(
        "videobridge.ice.advertise-private-candidates".from(ConfabConfig.newConfig)
    )

    companion object {
        @JvmField
        val config = IceConfig()
    }
}
