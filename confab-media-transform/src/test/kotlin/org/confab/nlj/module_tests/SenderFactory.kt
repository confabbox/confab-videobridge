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

package org.confab.nlj.module_tests

import org.confab.nlj.RtpSender
import org.confab.nlj.RtpSenderImpl
import org.confab.nlj.SetLocalSsrcEvent
import org.confab.nlj.format.PayloadType
import org.confab.nlj.resources.logging.StdoutLogger
import org.confab.nlj.rtcp.RtcpEventNotifier
import org.confab.nlj.rtp.RtpExtension
import org.confab.nlj.util.RemoteSsrcAssociation
import org.confab.nlj.util.StreamInformationStoreImpl
import org.confab.test_utils.SourceAssociation
import org.confab.test_utils.SrtpData
import org.confab.utils.MediaType
import org.confab.utils.logging2.Logger
import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

class SenderFactory {
    companion object {
        fun createSender(
            executor: ExecutorService,
            backgroundExecutor: ScheduledExecutorService,
            srtpData: SrtpData,
            payloadTypes: List<PayloadType>,
            headerExtensions: List<RtpExtension>,
            ssrcAssociations: List<SourceAssociation>,
            logger: Logger = StdoutLogger()
        ): RtpSender {
            val streamInformationStore = StreamInformationStoreImpl()
            val sender = RtpSenderImpl(
                Random().nextLong().toString(),
                RtcpEventNotifier(),
                executor,
                backgroundExecutor,
                streamInformationStore,
                logger
            )
            sender.setSrtpTransformers(SrtpTransformerFactory.createSrtpTransformers(srtpData))

            payloadTypes.forEach {
                streamInformationStore.addRtpPayloadType(it)
            }
            headerExtensions.forEach {
                streamInformationStore.addRtpExtensionMapping(it)
            }
            ssrcAssociations.forEach {
                streamInformationStore.addSsrcAssociation(
                    RemoteSsrcAssociation(it.primarySsrc, it.secondarySsrc, it.associationType)
                )
            }

            // Set some dummy sender SSRCs so RTCP can be forwarded
            sender.handleEvent(SetLocalSsrcEvent(MediaType.VIDEO, 123))
            sender.handleEvent(SetLocalSsrcEvent(MediaType.AUDIO, 456))

            return sender
        }
    }
}
