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

import com.github.almightysatan.motdify.exceptions.UnexpectedPacketException
import com.github.almightysatan.motdify.packets.ClientboundPacket
import com.github.almightysatan.motdify.packets.ServerboundPacket
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates

class Connection(val socket: Socket) : AutoCloseable {
    private val inputChannel = MinecraftDataInputChannel(socket.openReadChannel())
    private val outputChannel = socket.openWriteChannel()

    private val byteArrayOutputStream = ByteArrayOutputStream()
    private val dataOutputStream = MinecraftDataOutputStream(byteArrayOutputStream)

    var state = State.HANDSHAKE
    var protocol by Delegates.notNull<Int>()

    suspend fun <T: ServerboundPacket> readPacket(packet: T): T {
        inputChannel.readVarInt() // length
        val id = inputChannel.readVarInt()

        if (packet.id != id)
            throw UnexpectedPacketException(id)

        packet.read(inputChannel)
        LOGGER.info("Packet received ${socket.remoteAddress} $packet")
        return packet
    }

    suspend fun sendPacket(packet: ClientboundPacket) {
        dataOutputStream.writeVarInt(packet.id)
        packet.write(dataOutputStream)

        val bytes = byteArrayOutputStream.toByteArray()
        VarInt.writeVarInt(outputChannel::writeByte, bytes.size)
        outputChannel.writeAvailable(bytes)
        outputChannel.flush()
        byteArrayOutputStream.reset()
    }

    override fun close() {
        if (state != State.CLOSED) {
            state = State.CLOSED
            socket.close()
            outputChannel.close()
            dataOutputStream.close()
        }
    }
}
