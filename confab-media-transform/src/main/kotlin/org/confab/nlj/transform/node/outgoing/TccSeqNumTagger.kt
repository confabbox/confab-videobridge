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
import org.confab.nlj.rtp.RtpExtensionType.TRANSPORT_CC
import org.confab.nlj.rtp.TransportCcEngine
import org.confab.nlj.rtp.VideoRtpPacket
import org.confab.nlj.stats.NodeStatsBlock
import org.confab.nlj.transform.node.ModifierNode
import org.confab.nlj.util.ReadOnlyStreamInformationStore
import org.confab.nlj.util.bytes
import org.confab.rtp.rtp.RtpPacket
import org.confab.rtp.rtp.header_extensions.TccHeaderExtension
import java.lang.ref.WeakReference

class TccSeqNumTagger(
    transportCcEngine: TransportCcEngine? = null,
    streamInformationStore: ReadOnlyStreamInformationStore
) : ModifierNode("TCC sequence number tagger") {
    private var currTccSeqNum: Int = 1
    private var tccExtensionId: Int? = null

    init {
        streamInformationStore.onRtpExtensionMapping(TRANSPORT_CC) {
            tccExtensionId = it
        }
    }

    private val weakTcc = WeakReference(transportCcEngine)

    override fun modify(packetInfo: PacketInfo): PacketInfo {
        tccExtensionId?.let { tccExtId ->
            when (val rtpPacket = packetInfo.packetAs<RtpPacket>()) {
                is VideoRtpPacket -> {
                    val ext = rtpPacket.getHeaderExtension(tccExtId)
                        ?: rtpPacket.addHeaderExtension(tccExtId, TccHeaderExtension.DATA_SIZE_BYTES)

                    TccHeaderExtension.setSequenceNumber(ext, currTccSeqNum)

                    val curSeq = currTccSeqNum
                    val len = rtpPacket.length.bytes
                    packetInfo.onSent { weakTcc.get()?.mediaPacketSent(curSeq, len) }

                    currTccSeqNum++
                }
                else -> Unit
            }
        }

        return packetInfo
    }

    override fun getNodeStats(): NodeStatsBlock {
        return super.getNodeStats().apply {
            addString("tcc_ext_id", tccExtensionId.toString())
        }
    }

    override fun trace(f: () -> Unit) = f.invoke()
}
