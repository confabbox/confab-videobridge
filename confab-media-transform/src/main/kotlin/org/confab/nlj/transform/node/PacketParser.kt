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

import org.confab.nlj.PacketInfo
import org.confab.rtp.Packet
import org.confab.utils.logging2.Logger
import org.confab.utils.logging2.createChildLogger

open class PacketParser(
    name: String,
    parentLogger: Logger,
    private val action: (Packet) -> Packet
) : TransformerNode(name) {
    private val logger = createChildLogger(parentLogger)

    override fun transform(packetInfo: PacketInfo): PacketInfo? {
        return try {
            packetInfo.packet = action(packetInfo.packet)
            packetInfo.resetPayloadVerification()
            packetInfo
        } catch (e: Exception) {
            logger.warn("Error parsing packet: $e")
            null
        }
    }

    override fun trace(f: () -> Unit) = f.invoke()
}