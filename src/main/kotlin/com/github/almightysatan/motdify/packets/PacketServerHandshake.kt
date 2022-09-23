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

package com.github.almightysatan.motdify.packets

import com.github.almightysatan.motdify.MinecraftDataInputChannel
import kotlin.properties.Delegates

class PacketServerHandshake : ServerboundPacket {

    override val id = 0

    var protocol by Delegates.notNull<Int>()
    lateinit var address: String
    var port by Delegates.notNull<UShort>()
    var nextState by Delegates.notNull<Int>()

    override suspend fun read(stream: MinecraftDataInputChannel) {
        protocol = stream.readVarInt()
        address = stream.readString(255)
        port = stream.readUShort()
        nextState = stream.readVarInt()
    }

    override fun toString(): String {
        return "PacketServerHandshake(id=$id, protocol=$protocol, address='$address', port=$port, nextState=$nextState)"
    }
}