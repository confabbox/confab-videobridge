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

package org.confab.nlj.rtcp

import org.confab.nlj.rtp.bandwidthestimation.BandwidthEstimator
import org.confab.nlj.util.ReadOnlyStreamInformationStore
import org.confab.nlj.util.bps
import org.confab.rtp.rtcp.RtcpPacket
import org.confab.rtp.rtcp.rtcpfb.payload_specific_fb.RtcpFbRembPacket
import org.confab.utils.logging2.Logger
import org.confab.utils.logging2.createChildLogger
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class RembHandler(
    val streamInformationStore: ReadOnlyStreamInformationStore,
    parentLogger: Logger
) : RtcpListener {
    private val logger = createChildLogger(parentLogger)
    private var sawSpuriousRemb = false

    private val bweUpdateListeners: MutableList<BandwidthEstimator.Listener> =
        CopyOnWriteArrayList()

    override fun rtcpPacketReceived(packet: RtcpPacket?, receivedTime: Instant?) {
        if (packet is RtcpFbRembPacket) {
            logger.debug { "Received REMB packet" }
            onRembPacket(packet)
        }
    }

    fun addListener(bweUpdateListener: BandwidthEstimator.Listener) {
        bweUpdateListeners.add(bweUpdateListener)
    }

    private fun onRembPacket(rembPacket: RtcpFbRembPacket) {
        if (streamInformationStore.supportsTcc) {
            if (!sawSpuriousRemb) {
                logger.warn {
                    "Ignoring unexpected REMB, when using TCC (for ${rembPacket.bitrate.bps} bps). Will " +
                        "suppress future logs for this endpoint."
                }
                endpointsWithSpuriousRemb.incrementAndGet()
                sawSpuriousRemb = true
            }
            return
        }
        logger.debug { "Updating bandwidth to ${rembPacket.bitrate.bps}" }
        bweUpdateListeners.forEach { it.bandwidthEstimationChanged(rembPacket.bitrate.bps) }
    }

    companion object {
        private val endpointsWithSpuriousRemb = AtomicInteger()

        fun endpointsWithSpuriousRemb() = endpointsWithSpuriousRemb.get()
    }
}
