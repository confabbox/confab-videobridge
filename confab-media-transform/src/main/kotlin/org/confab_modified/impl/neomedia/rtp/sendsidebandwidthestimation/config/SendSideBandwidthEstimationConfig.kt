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

package org.confab_modified.impl.neomedia.rtp.sendsidebandwidthestimation.config

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import org.confab.nlj.util.Bandwidth
import org.confab.nlj.util.kbps

class SendSideBandwidthEstimationConfig {
    companion object {
        private val defaultLowLossThreshold: Double by
        config("jmt.bwe.send-side.low-loss-threshold".from(ConfabConfig.newConfig))

        /**
         * The low-loss threshold (expressed as a proportion of lost packets) when the loss probability
         * experiment is *not* active
         */
        @JvmStatic
        fun defaultLowLossThreshold() = defaultLowLossThreshold

        private val defaultHighLossThreshold: Double by
        config("jmt.bwe.send-side.high-loss-threshold".from(ConfabConfig.newConfig))

        /**
         * The high-loss threshold (expressed as a proportion of lost packets) when the loss probability
         * experiment is *not* active.
         */
        @JvmStatic
        fun defaultHighLossThreshold() = defaultHighLossThreshold

        private val defaultBitrateThreshold: Bandwidth by config {
            "jmt.bwe.send-side.bitrate-threshold".from(ConfabConfig.newConfig)
                .convertFrom<String> { Bandwidth.fromString(it) }
        }
        /**
         * The bitrate threshold when the loss probability experiment is *not* active.
         */
        @JvmStatic
        fun defaultBitrateThreshold() = defaultBitrateThreshold

        @JvmStatic
        fun defaultBitrateThresholdBps() = defaultBitrateThreshold.bps

        private const val LEGACY_BASE_NAME =
            "org.confab.impl.neomedia.rtp.sendsidebandwidthestimation.SendSideBandwidthEstimation"

        private val lossExperimentProbability: Double by config {
            "$LEGACY_BASE_NAME.lossExperimentProbability".from(ConfabConfig.legacyConfig)
            "jmt.bwe.send-side.loss-experiment.probability".from(ConfabConfig.newConfig)
        }

        /**
         * The probability of enabling the loss-based experiment.
         */
        @JvmStatic
        fun lossExperimentProbability() = lossExperimentProbability

        private val experimentalLowLossThreshold: Double by config {
            "$LEGACY_BASE_NAME.lowLossThreshold".from(ConfabConfig.legacyConfig)
            "jmt.bwe.send-side.loss-experiment.low-loss-threshold".from(ConfabConfig.newConfig)
        }

        /**
         * The low-loss threshold (expressed as a proportion of lost packets) when the loss probability
         * experiment is active.
         */
        @JvmStatic
        fun experimentalLowLossThreshold() = experimentalLowLossThreshold

        private val experimentalHighLossThreshold: Double by config {
            "$LEGACY_BASE_NAME.highLossThreshold".from(ConfabConfig.legacyConfig)
            "jmt.bwe.send-side.loss-experiment.high-loss-threshold".from(ConfabConfig.newConfig)
        }

        /**
         * The high-loss threshold (expressed as a proportion of lost packets).
         */
        @JvmStatic
        fun experimentalHighLossThreshold() = experimentalHighLossThreshold

        private val experimentalBitrateThreshold: Bandwidth by config {
            "$LEGACY_BASE_NAME.bitrateThresholdKbps".from(ConfabConfig.legacyConfig).convertFrom<Int> { it.kbps }
            "jmt.bwe.send-side.loss-experiment.bitrate-threshold".from(ConfabConfig.newConfig)
                .convertFrom<String> { Bandwidth.fromString(it) }
        }

        /**
         * The bitrate threshold when the loss probability experiment is active.
         */
        @JvmStatic
        fun experimentalBitrateThreshold() = experimentalBitrateThreshold

        @JvmStatic
        fun experimentalBitrateThresholdBps() = experimentalBitrateThreshold.bps

        private val timeoutExperimentProbability: Double by config {
            "$LEGACY_BASE_NAME.timeoutExperimentProbability".from(ConfabConfig.legacyConfig)
            "jmt.bwe.send-side.timeout-experiment.probability".from(ConfabConfig.newConfig)
        }

        /**
         * The probability of enabling the timeout experiment.
         */
        @JvmStatic
        fun timeoutExperimentProbability() = timeoutExperimentProbability
    }
}
