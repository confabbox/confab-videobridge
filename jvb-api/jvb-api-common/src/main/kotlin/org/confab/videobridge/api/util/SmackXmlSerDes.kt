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

package org.confab.videobridge.api.util

import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.util.PacketParserUtils

/**
 * Functions to serialize and deserialize Smack XMPP [Stanza]s to/from
 * [String]s.  Note that deserialization relies on the proper
 * [org.jivesoftware.smack.provider.Provider]s being installed to the
 * [org.jivesoftware.smack.provider.ProviderManager].
 */
class SmackXmlSerDes {
    companion object {
        fun serialize(stanza: Stanza): String =
            stanza.toXML().toString()

        fun deserialize(data: String): Stanza {
            return PacketParserUtils.parseStanza(data)
        }
    }
}