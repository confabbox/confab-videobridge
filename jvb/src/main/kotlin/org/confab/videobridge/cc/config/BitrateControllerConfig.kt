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
import org.confab.metaconfig.from
import org.confab.nlj.util.Bandwidth
import java.time.Duration

class BitrateControllerConfig private constructor() {
    /**
     * The bandwidth estimation threshold.
     *
     * In order to limit the resolution changes due to bandwidth changes we only react to bandwidth changes greater
     * than {@code bweChangeThreshold * last_bandwidth_estimation}.
     */
    private val bweChangeThreshold: Double by config {
        "org.confab.videobridge.BWE_CHANGE_THRESHOLD_PCT".from(ConfabConfig.legacyConfig)
            .transformedBy { it / 100.0 }
        // This is an old version, include for backward compat.
        "videobridge.cc.bwe-change-threshold-pct".from(ConfabConfig.newConfig)
            .transformedBy { it / 100.0 }
        "videobridge.cc.bwe-change-threshold".from(ConfabConfig.newConfig)
    }
    fun bweChangeThreshold() = bweChangeThreshold

    /**
     * The max resolution to allocate for the thumbnails.
     */
    private val thumbnailMaxHeightPx: Int by config {
        "org.confab.videobridge.THUMBNAIL_MAX_HEIGHT".from(ConfabConfig.legacyConfig)
        "videobridge.cc.thumbnail-max-height-px".from(ConfabConfig.newConfig)
    }
    fun thumbnailMaxHeightPx() = thumbnailMaxHeightPx

    /**
     * The default preferred resolution to allocate for the onstage participant,
     * before allocating bandwidth for the thumbnails.
     */
    private val onstagePreferredHeightPx: Int by config {
        "org.confab.videobridge.ONSTAGE_PREFERRED_HEIGHT".from(ConfabConfig.legacyConfig)
        "videobridge.cc.onstage-preferred-height-px".from(ConfabConfig.newConfig)
    }
    fun onstagePreferredHeightPx() = onstagePreferredHeightPx

    /**
     * The preferred frame rate to allocate for the onstage participant.
     */
    private val onstagePreferredFramerate: Double by config {
        "org.confab.videobridge.ONSTAGE_PREFERRED_FRAME_RATE".from(ConfabConfig.legacyConfig)
        "videobridge.cc.onstage-preferred-framerate".from(ConfabConfig.newConfig)
    }
    fun onstagePreferredFramerate() = onstagePreferredFramerate

    /**
     * Whether or not we are allowed to oversend (exceed available bandwidth) for the video of the on-stage
     * participant.
     */
    private val allowOversendOnStage: Boolean by config {
        "org.confab.videobridge.ENABLE_ONSTAGE_VIDEO_SUSPEND".from(ConfabConfig.legacyConfig).transformedBy { !it }
        "videobridge.cc.enable-onstage-video-suspend".from(ConfabConfig.newConfig).transformedBy { !it }
        "videobridge.cc.allow-oversend-onstage".from(ConfabConfig.newConfig)
    }
    fun allowOversendOnStage(): Boolean = allowOversendOnStage

    /**
     * The maximum bitrate by which the bridge may exceed the estimated available bandwidth when oversending.
     */
    private val maxOversendBitrate: Bandwidth by config {
        "videobridge.cc.max-oversend-bitrate".from(ConfabConfig.newConfig)
            .convertFrom<String> { Bandwidth.fromString(it) }
    }
    fun maxOversendBitrateBps(): Double = maxOversendBitrate.bps

    /**
     * Whether or not we should trust the bandwidth
     * estimations. If this is se to false, then we assume a bandwidth
     * estimation of Long.MAX_VALUE.
     */
    private val trustBwe: Boolean by config {
        "org.confab.videobridge.TRUST_BWE".from(ConfabConfig.legacyConfig)
        "videobridge.cc.trust-bwe".from(ConfabConfig.newConfig)
    }
    fun trustBwe(): Boolean = trustBwe

    /**
     * The property for the max resolution to allocate for the onstage
     * participant.
     */
    private val onstageIdealHeightPx: Int by config(
        "videobridge.cc.onstage-ideal-height-px".from(ConfabConfig.newConfig)
    )
    fun onstageIdealHeightPx() = onstageIdealHeightPx

    /**
     * The maximum amount of time we'll run before recalculating which streams we'll
     * forward.
     */
    private val maxTimeBetweenCalculations: Duration by config(
        "videobridge.cc.max-time-between-calculations".from(ConfabConfig.newConfig)
    )
    fun maxTimeBetweenCalculations() = maxTimeBetweenCalculations

    companion object {
        @JvmField
        val config = BitrateControllerConfig()
    }
}
