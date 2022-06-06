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

package org.confab.videobridge.xmpp.config

import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import org.confab.config.ConfabConfig
import org.confab.metaconfig.ConfigException
import org.confab.metaconfig.config
import org.confab.videobridge.stats.config.StatsManagerConfig
import org.confab.videobridge.stats.config.StatsTransportConfig
import org.confab.xmpp.mucclient.MucClientConfiguration
import java.time.Duration

class XmppClientConnectionConfig private constructor() {
    val clientConfigs: List<MucClientConfiguration> by config {
        "org.confab.videobridge.xmpp.user."
            .from(ConfabConfig.legacyConfig)
            .convertFrom<Map<String, String>> { propsMap ->
                MucClientConfiguration.loadFromMap(propsMap, "org.confab.videobridge.xmpp.user.", true)
                    .toList()
                    .apply { forEach { it.applyDefaultIqHandlerMode() } }
                    .takeIf { it.isNotEmpty() } ?: throw ConfigException.UnableToRetrieve.NotFound("no configs found")
            }
        "videobridge.apis.xmpp-client.configs".from(ConfabConfig.newConfig)
            .convertFrom<ConfigObject> { cfg ->
                cfg.entries
                    .map { it.toMucClientConfiguration() }
                    .filter { it.isComplete }
            }
    }

    private val presenceIntervalProperty: Duration by config {
        "videobridge.apis.xmpp-client.presence-interval".from(ConfabConfig.newConfig)
    }

    /**
     * The interval at which presence updates (with updates stats/status) are published. Allow to be overridden by
     * legacy-style "stats-transports" config.
     */
    val presenceInterval: Duration = StatsManagerConfig.config.transportConfigs.stream()
        .filter { tc -> tc is StatsTransportConfig.MucStatsTransportConfig }
        .map(StatsTransportConfig::interval)
        .findFirst()
        .orElse(presenceIntervalProperty)

    /**
     * Whether to filter the statistics.
     */
    val statsFilterEnabled: Boolean by config {
        "videobridge.apis.xmpp-client.stats-filter.enabled".from(ConfabConfig.newConfig)
    }

    /**
     * The set of statistics to send, when filtering is enabled.
     */
    val statsWhitelist: List<String> by config {
        "videobridge.apis.xmpp-client.stats-filter.whitelist".from(ConfabConfig.newConfig)
    }

    /**
     * The size to set for Smack's JID cache
     */
    val jidCacheSize: Int by config {
        "videobridge.apis.xmpp-client.jid-cache-size".from(ConfabConfig.newConfig)
    }

    companion object {
        @JvmField
        val config = XmppClientConnectionConfig()
    }
}

/**
 * We want the bridge to default to using "sync" as the IQ handler mode (unless the config actually overrides it).
 */
private fun MucClientConfiguration.applyDefaultIqHandlerMode() {
    if (this.iqHandlerMode == null) {
        this.iqHandlerMode = "sync"
    }
}

private fun MutableMap.MutableEntry<String, ConfigValue>.toMucClientConfiguration(): MucClientConfiguration {
    val config = MucClientConfiguration(this.key)
    (value as? ConfigObject)?.let {
        it.forEach { (propName, propValue) ->
            config.setProperty(propName, propValue.unwrapped().toString())
        }
    } ?: run {
        throw Exception(
            "Invalid muc client configuration. " +
                "Expected type ConfigObject but got ${value.unwrapped()::class.java}"
        )
    }

    return config.also { it.applyDefaultIqHandlerMode() }
}
