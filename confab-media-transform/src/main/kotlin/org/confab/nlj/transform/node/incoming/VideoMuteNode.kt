package org.confab.nlj.transform.node.incoming

import org.confab.nlj.PacketInfo
import org.confab.nlj.rtp.VideoRtpPacket
import org.confab.nlj.stats.NodeStatsBlock
import org.confab.nlj.transform.node.ObserverNode

class VideoMuteNode : ObserverNode("Video mute node") {

    private var numMutedPackets = 0
    var forceMute: Boolean = false

    override fun observe(packetInfo: PacketInfo) {
        if (packetInfo.packet !is VideoRtpPacket) return
        if (this.forceMute) {
            packetInfo.shouldDiscard = true
            numMutedPackets++
        }
    }

    override fun getNodeStats(): NodeStatsBlock = super.getNodeStats().apply {
        addNumber("num_video_packets_discarded", numMutedPackets)
    }

    override fun trace(f: () -> Unit) = f.invoke()
}
