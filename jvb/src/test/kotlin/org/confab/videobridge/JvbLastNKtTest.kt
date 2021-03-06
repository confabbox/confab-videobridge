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

package org.confab.videobridge

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class JvbLastNKtTest : ShouldSpec({
    context("calculateLastN") {
        should("return the correct result") {
            calculateLastN(-1, -1) shouldBe -1
            calculateLastN(-1, 10) shouldBe 10
            calculateLastN(10, -1) shouldBe 10
            calculateLastN(2, 3) shouldBe 2
            calculateLastN(-1, -1, -1) shouldBe -1
            calculateLastN(-1, 10, -1) shouldBe 10
            calculateLastN(2, 3, 3) shouldBe 2
            calculateLastN(2, 3, 33) shouldBe 2
        }
    }
})
