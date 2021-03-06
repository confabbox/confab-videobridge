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

package org.confab.videobridge.load_management

import org.confab.config.ConfabConfig
import org.confab.metaconfig.config
import org.confab.metaconfig.from
import org.confab.nlj.util.NEVER
import org.confab.utils.OrderedJsonObject
import org.confab.utils.logging2.cdebug
import org.confab.utils.logging2.createLogger
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.logging.Level

class JvbLoadManager<T : JvbLoadMeasurement> @JvmOverloads constructor(
    private val jvbLoadThreshold: T,
    private val jvbRecoveryThreshold: T,
    private val loadReducer: JvbLoadReducer,
    private val clock: Clock = Clock.systemUTC()
) {
    private val logger = createLogger(minLogLevel = Level.ALL)

    val reducerEnabled: Boolean by config("videobridge.load-management.reducer-enabled".from(ConfabConfig.newConfig))

    private var lastReducerTime: Instant = NEVER

    private var state: State = State.NOT_OVERLOADED

    private var mostRecentLoadMeasurement: T? = null

    fun loadUpdate(loadMeasurement: T) {
        logger.cdebug { "Got a load measurement of $loadMeasurement" }
        mostRecentLoadMeasurement = loadMeasurement
        val now = clock.instant()
        if (loadMeasurement.getLoad() >= jvbLoadThreshold.getLoad()) {
            state = State.OVERLOADED
            if (reducerEnabled) {
                logger.info("Load measurement $loadMeasurement is above threshold of $jvbLoadThreshold")
                if (canRunReducer(now)) {
                    logger.info("Running load reducer")
                    loadReducer.reduceLoad()
                    lastReducerTime = now
                } else {
                    logger.info(
                        "Load reducer ran at $lastReducerTime, which is within " +
                            "${loadReducer.impactTime()} of now, not running reduce"
                    )
                }
            }
        } else {
            state = State.NOT_OVERLOADED
            if (reducerEnabled) {
                if (loadMeasurement.getLoad() < jvbRecoveryThreshold.getLoad()) {
                    if (canRunReducer(now)) {
                        if (loadReducer.recover()) {
                            logger.info(
                                "Recovery ran after a load measurement of $loadMeasurement (which was " +
                                    "below threshold of $jvbRecoveryThreshold) was received"
                            )
                            lastReducerTime = now
                        } else {
                            logger.cdebug { "Recovery had no work to do" }
                        }
                    } else {
                        logger.cdebug {
                            "Load measurement $loadMeasurement is below recovery threshold, but load reducer " +
                                "ran at $lastReducerTime, which is within ${loadReducer.impactTime()} of now, " +
                                "not running recover"
                        }
                    }
                }
            }
        }
    }

    fun getCurrentStressLevel(): Double =
        mostRecentLoadMeasurement?.div(jvbLoadThreshold) ?: 0.0

    fun getStats() = OrderedJsonObject().apply {
        put("state", state.toString())
        put("stress", getCurrentStressLevel().toString())
        put("reducer_enabled", reducerEnabled.toString())
        put("reducer", loadReducer.getStats())
    }

    private fun canRunReducer(now: Instant): Boolean =
        Duration.between(lastReducerTime, now) >= loadReducer.impactTime()

    enum class State {
        OVERLOADED,
        NOT_OVERLOADED
    }

    companion object {
        val averageParticipantStress: Double by config {
            "videobridge.load-management.average-participant-stress".from(ConfabConfig.newConfig)
        }
    }
}
