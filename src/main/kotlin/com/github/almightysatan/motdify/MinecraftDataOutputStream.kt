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
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class MinecraftDataOutputStream(outputStream: OutputStream) : AutoCloseable {

    private val stream = DataOutputStream(outputStream)

    suspend fun writeVarInt(value: Int) {
        VarInt.writeVarInt({stream.writeByte(it.toInt())}, value)
    }

    fun writeLong(value: Long) {
        this.stream.writeLong(value)
    }

    suspend fun writeString(value: String) {
        if (value.length > STRING_MAX_LENGTH)
            throw InvalidStringSizeException(value.length)
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        this.writeVarInt(bytes.size)
        this.stream.write(bytes)
    }

    override fun close() {
        this.stream.close()
    }
}