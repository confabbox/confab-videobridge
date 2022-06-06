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
package org.confab.nlj.transform.node

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import org.confab.nlj.PacketInfo
import org.confab.utils.logging2.Logger
import java.util.Date

class ToggleablePcapWriter(
    private val parentLogger: Logger,
    private val prefix: String
) {
    private var pcapWriter: PcapWriter? = null
    private val pcapLock = Any()

    fun enable() {
        if (!allowed) {
            throw IllegalStateException("PCAP capture is disabled in configuration")
        }

        synchronized(pcapLock) {
            if (pcapWriter == null) {
                pcapWriter = PcapWriter(parentLogger, "/tmp/$prefix-${Date().toInstant()}.pcap")
            }
        }
    }

    fun disable() {
        synchronized(pcapLock) {
            pcapWriter?.close()
            pcapWriter = null
        }
    }

    fun isEnabled(): Boolean = pcapWriter != null

    fun newObserverNode(): Node = PcapWriterNode("Toggleable pcap writer: $prefix")

    private inner class PcapWriterNode(name: String) : ObserverNode(name) {
        override fun observe(packetInfo: PacketInfo) {
            pcapWriter?.processPacket(packetInfo)
        }

        override fun trace(f: () -> Unit) = f.invoke()
    }

    companion object {
        private val allowed: Boolean by config("jmt.debug.pcap.enabled".from(ConfabConfig.newConfig))
    }
}
