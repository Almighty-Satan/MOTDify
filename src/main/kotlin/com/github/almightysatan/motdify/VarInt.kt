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

import com.github.almightysatan.motdify.exceptions.InvalidVarIntException
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

object VarInt {

    private const val VAR_INT_NUM_CONTENT_BITS = 7
    private const val VAR_INT_CONTENT_BITS = 0x7F.toByte()
    private const val VAR_INT_END_NUMBER_BIT = 0x80.toByte()
    private const val VAR_INT_MAX_LENGTH = 5

    suspend fun readVarInt(byteReadFunction: suspend () -> Byte) : Int {
        var value = 0
        for (i in 0 until VAR_INT_MAX_LENGTH) {
            val byte = byteReadFunction()
            value = value.or(byte.and(VAR_INT_CONTENT_BITS).toInt().shl(i * VAR_INT_NUM_CONTENT_BITS))

            if ((byte.and(VAR_INT_END_NUMBER_BIT)) == 0.toByte()) // End of var int
                return value
        }
        throw InvalidVarIntException()
    }

    suspend fun writeVarInt(byteWriteFunction: suspend (Byte) -> Unit, value: Int) {
        var remaining = value
        for (i in 0 until VAR_INT_MAX_LENGTH) {
            val byte = remaining.toByte().and(VAR_INT_CONTENT_BITS)
            remaining = remaining.and(byte.inv().toInt()).ushr(VAR_INT_NUM_CONTENT_BITS)
            if (remaining == 0) {
                byteWriteFunction(byte)
                return
            }
            byteWriteFunction(byte.or(VAR_INT_END_NUMBER_BIT))
        }
        throw Error()
    }
}
