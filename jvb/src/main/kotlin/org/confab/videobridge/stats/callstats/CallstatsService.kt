/*
 * Copyright @ 2020 - Present, 8x8 Inc
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

package org.confab.videobridge.stats.callstats

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.optionalconfig
import org.confab.stats.media.StatsService
import org.confab.stats.media.StatsServiceFactory
import org.confab.utils.logging2.createLogger
import org.confab.utils.version.Version
import org.confab.videobridge.Videobridge
import org.confab.videobridge.stats.StatsTransport
import org.confab.videobridge.stats.callstats.CallstatsConfig.Companion.config
import org.confab.videobridge.stats.config.StatsManagerConfig
import org.confab.videobridge.stats.config.StatsTransportConfig
import java.time.Duration

class CallstatsService(
    /**
     * The version of the running application.
     */
    val version: Version
) {
    private val logger = createLogger()

    /**
     * The entry point into the confab-stats library used to send stats to callstats. It is initialized asynchronously.
     */
    private var statsService: StatsService? = null

    /**
     * The handler for conference created/expired events, which enables sending of per-conference statistics.
     * Initialized asynchronously.
     */
    private var conferenceManager: CallstatsConferenceManager? = null

    /**
     * The [StatsTransport] used to send global stats to callstats. Initialized asynchronously, and only if the stats
     * manager is available to provide stats.
     */
    private var callstatsTransport: CallstatsTransport? = null

    val statsTransport: StatsTransport?
        get() = callstatsTransport

    fun start(
        /**
         * Function to call if and when the service successfully initializes.
         */
        initializedCallback: () -> Unit = {}
    ) {
        logger.info("Starting CallstatsService with config: $config")

        // as we create only one instance of StatsService
        StatsServiceFactory.getInstance().createStatsService(
            version,
            config.appId,
            config.appSecret,
            config.keyId,
            config.keyPath,
            config.bridgeId,
            /* isClient = */ false,
            object : StatsServiceFactory.InitCallback {
                override fun error(reason: String, message: String) {
                    logger.error(
                        "Confab-stats service failed to initialize with reason: $reason and error message: $message "
                    )
                }

                override fun onInitialized(statsService: StatsService, message: String) {
                    logger.info("Confab-stats service initialized: $message")
                    statsServiceInitialized(statsService, initializedCallback)
                }
            }
        )
    }

    fun statsServiceInitialized(statsService: StatsService, callback: () -> Unit) {
        // Now that the callstats/confab-stats service has been initialized, we can hook up to global statistics and
        // conference create/expire events from [Videobridge]

        this.statsService = statsService

        callstatsTransport = CallstatsTransport(statsService)
        conferenceManager =
            CallstatsConferenceManager(
                statsService,
                config.bridgeId,
                config.interval.toMillis(),
                config.conferenceIdPrefix
            )

        callback()
    }

    fun stop() {
        logger.info("Stopping CallstatsService")
        conferenceManager?.let {
            it.stop()
        }

        conferenceManager = null
        callstatsTransport = null
        statsService = null
    }

    val videobridgeEventHandler: Videobridge.EventHandler?
        get() = conferenceManager
}

class CallstatsConfig private constructor() {
    /**
     * The callstats AppID.
     */
    val appId: Int by config {
        "io.callstats.sdk.CallStats.appId".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.app-id".from(ConfabConfig.newConfig)
    }

    /**
     * Shared Secret for authentication on Callstats.io
     */
    val appSecret: String? by optionalconfig {
        "io.callstats.sdk.CallStats.appSecret".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.app-secret".from(ConfabConfig.newConfig)
    }

    /**
     * ID of the key that was used to generate token.
     */
    val keyId: String? by optionalconfig {
        "io.callstats.sdk.CallStats.keyId".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.key-id".from(ConfabConfig.newConfig)
    }

    /**
     * The path to private key file.
     */
    val keyPath: String? by optionalconfig {
        "io.callstats.sdk.CallStats.keyPath".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.key-path".from(ConfabConfig.newConfig)
    }

    val bridgeId: String by config {
        "io.callstats.sdk.CallStats.bridgeId".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.bridge-id".from(ConfabConfig.newConfig)
    }

    /**
     * The bridge conference prefix to report to callstats.io.
     */
    val conferenceIdPrefix: String? by optionalconfig {
        "io.callstats.sdk.CallStats.conferenceIDPrefix".from(ConfabConfig.legacyConfig)
        "videobridge.stats.callstats.conference-id-prefix".from(ConfabConfig.newConfig)
    }

    private val intervalProperty: Duration by config {
        "videobridge.stats.callstats.interval".from(ConfabConfig.newConfig)
    }

    /**
     * This is the interval at which stats are pushed to callstats. It affects both global and per-conference stats.
     *
     * For backwards compatibility, we read it from the stats manager "callstatsio" transport, if present.
     */
    val interval: Duration
        get() = StatsManagerConfig.config.transportConfigs.stream()
            .filter { tc -> tc is StatsTransportConfig.CallStatsIoStatsTransportConfig }
            .map(StatsTransportConfig::interval)
            .findFirst()
            .orElse(intervalProperty)

    val enabled: Boolean
        get() = appId > 0

    override fun toString() = "appId=$appId, appSecret is ${if (appSecret == null) "unset" else "set"}, keyId=$keyId," +
        " keyPath=$keyPath, bridgeId=$bridgeId, conferenceIdPrefix=$conferenceIdPrefix, interval=$interval"

    companion object {
        @JvmField
        val config = CallstatsConfig()
    }
}
