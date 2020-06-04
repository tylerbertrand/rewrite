/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrewrite.text.ChangeText

class YamlProfileSourceTest {
    @Test
    fun loadYaml() {
        val profile = YamlProfileSource("""
            profile: test

            include:
              - 'org.openrewrite.text.*'

            exclude:
              - org.openrewrite.text.DoesNotExist

            configure:
              org.openrewrite.text.ChangeText:
                toText: 'Hello Jon!'
        """.trimIndent().byteInputStream()).load().first()

        val changeText = ChangeText()

        assertThat(profile.configure(changeText).toText).isEqualTo("Hello Jon!")
        assertThat(profile.accept(changeText)).isTrue()
    }

    @Test
    fun loadMultiYaml() {
        val profiles = YamlProfileSource("""
            ---
            profile: checkstyle
            ---
            profile: spring
        """.trimIndent().byteInputStream()).load()

        assertThat(profiles.map { it.name }).containsOnly("checkstyle", "spring")
    }
}
