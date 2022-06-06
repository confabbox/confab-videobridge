/*
 * Copyright @ 2019 - present 8x8 Inc
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
package org.confab.nlj.transform.node

import org.confab.nlj.PacketInfo
import org.confab.nlj.format.PayloadTypeEncoding.RED
import org.confab.nlj.rtp.AudioRtpPacket
import org.confab.nlj.rtp.RedAudioRtpPacket
import org.confab.nlj.rtp.VideoRtpPacket
import org.confab.nlj.util.ReadOnlyStreamInformationStore
import org.confab.rtp.rtp.RtpHeader
import org.confab.utils.MediaType
import org.confab.utils.logging2.Logger
import org.confab.utils.logging2.cdebug
import org.confab.utils.logging2.createChildLogger

class RtpParser(
    private val streamInformationStore: ReadOnlyStreamInformationStore,
    parentLogger: Logger
) : TransformerNode("RTP Parser") {
    private val logger = createChildLogger(parentLogger)

    override fun transform(packetInfo: PacketInfo): PacketInfo? {
        val packet = packetInfo.packet
        val payloadTypeNumber = RtpHeader.getPayloadType(packet.buffer, packet.offset).toByte()

        val payloadType = streamInformationStore.rtpPayloadTypes[payloadTypeNumber] ?: run {
            logger.cdebug { "Unknown payload type: $payloadTypeNumber" }
            return null
        }

        packetInfo.packet = when (payloadType.mediaType) {
            MediaType.AUDIO -> when (payloadType.encoding) {
                RED -> packet.toOtherType(::RedAudioRtpPacket)
                else -> packet.toOtherType(::AudioRtpPacket)
            }
            MediaType.VIDEO -> packet.toOtherType(::VideoRtpPacket)
            else -> throw Exception("Unrecognized media type: '${payloadType.mediaType}'")
        }

        packetInfo.resetPayloadVerification()
        return packetInfo
    }

    override fun trace(f: () -> Unit) = f.invoke()
}
