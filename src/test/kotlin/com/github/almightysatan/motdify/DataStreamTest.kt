/*
 * MOTDify
 * Copyright (C) 2022 Almighty-Satan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.almightysatan.motdify

import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStreamTest {

    @Test
    fun testVarIntZero() {
        testVarInt(0)
    }

    @Test
    fun testVarIntOne() {
        testVarInt(1)
    }

    @Test
    fun testVarIntMinusOne() {
        testVarInt(-1)
    }

    @Test
    fun testVarInt255() {
        testVarInt(0)
    }

    @Test
    fun testVarIntIntegerMax() {
        testVarInt(Int.MAX_VALUE)
    }

    private fun testVarInt(value: Int) {
        runBlocking {
            val byteOutputStream = ByteArrayOutputStream()

            VarInt.writeVarInt({byteOutputStream.write(it.toInt())}, value)

            val bytes = byteOutputStream.toByteArray()
            val byteInputStream = ByteArrayInputStream(bytes)

            byteOutputStream.close()
            byteInputStream.close()

            assertEquals(value, VarInt.readVarInt{byteInputStream.read().toByte()})
        }
    }
}
