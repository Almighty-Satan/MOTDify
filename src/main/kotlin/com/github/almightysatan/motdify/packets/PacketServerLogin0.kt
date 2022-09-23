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

class PacketServerLogin0 : ServerboundPacket {

    override val id = 0

    lateinit var name: String

    override suspend fun read(stream: MinecraftDataInputChannel) {
        name = stream.readString(16)
    }

    override fun toString(): String {
        return "PacketServerLogin0(id=$id, name='$name')"
    }
}
