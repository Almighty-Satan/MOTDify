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

import com.github.almightysatan.motdify.exceptions.InvalidStringSizeException
import io.ktor.utils.io.*
import java.nio.charset.StandardCharsets
import java.util.UUID

class MinecraftDataInputChannel(private val channel: ByteReadChannel) {

    suspend fun readByteArray(length: Int) : ByteArray {
        val buffer = ByteArray(length)
        channel.readAvailable(buffer)
        return buffer
    }

    suspend fun readBoolean() : Boolean {
        return this.channel.readByte() != 0.toByte()
    }

    suspend fun readVarInt() : Int {
        return VarInt.readVarInt(channel::readByte)
    }

    suspend fun readLong() : Long {
        return this.channel.readLong()
    }

    suspend fun readUShort() : UShort {
        return this.channel.readShort().toUShort()
    }

    suspend fun readString(maxLength: Int) : String {
        val size = this.readVarInt()
        if (size > maxLength * 4 || size > STRING_MAX_LENGTH * 4)
            throw InvalidStringSizeException(size)
        return String(this.readByteArray(size), StandardCharsets.UTF_8)
    }

    suspend fun readUUID() : UUID {
        return UUID(this.channel.readLong(), this.channel.readLong())
    }
}
