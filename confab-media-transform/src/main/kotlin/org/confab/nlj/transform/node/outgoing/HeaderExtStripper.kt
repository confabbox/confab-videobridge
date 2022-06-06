/*
 * Copyright @ 2018 - Present, 8x8 Inc
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
package org.confab.nlj.transform.node.outgoing

import org.confab.nlj.PacketInfo
import org.confab.nlj.rtp.RtpExtensionType
import org.confab.nlj.transform.node.ModifierNode
import org.confab.nlj.util.ReadOnlyStreamInformationStore
import org.confab.rtp.rtp.RtpPacket

/**
 * Strip all hop-by-hop header extensions.  Currently this leaves only ssrc-audio-level and video-orientation.
 */
class HeaderExtStripper(
    streamInformationStore: ReadOnlyStreamInformationStore
) : ModifierNode("Strip header extensions") {
    private var retainedExts: Set<Int> = emptySet()

    init {
        retainedExtTypes.forEach { rtpExtensionType ->
            streamInformationStore.onRtpExtensionMapping(rtpExtensionType) {
                it?.let { retainedExts = retainedExts.plus(it) }
            }
        }
    }

    override fun modify(packetInfo: PacketInfo): PacketInfo {
        val rtpPacket = packetInfo.packetAs<RtpPacket>()

        rtpPacket.removeHeaderExtensionsExcept(retainedExts)

        return packetInfo
    }

    override fun trace(f: () -> Unit) = f.invoke()

    companion object {
        private val retainedExtTypes: Set<RtpExtensionType> = setOf(
            RtpExtensionType.SSRC_AUDIO_LEVEL, RtpExtensionType.VIDEO_ORIENTATION
        )
    }
}
