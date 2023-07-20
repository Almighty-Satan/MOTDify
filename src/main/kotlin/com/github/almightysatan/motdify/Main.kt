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

import com.github.almightysatan.motdify.packets.PacketClientDisconnect
import com.github.almightysatan.motdify.packets.PacketClientPing
import com.github.almightysatan.motdify.packets.PacketClientStatus
import com.github.almightysatan.motdify.packets.PacketServerHandshake
import com.github.almightysatan.motdify.packets.PacketServerLogin0
import com.github.almightysatan.motdify.packets.PacketServerLogin759
import com.github.almightysatan.motdify.packets.PacketServerLogin761
import com.github.almightysatan.motdify.packets.PacketServerPing
import com.github.almightysatan.motdify.packets.PacketServerStatus
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.util.*

const val ONE_NINETEEN_PROTOCOL = 759
const val ONE_NINETEEN_THREE_PROTOCOL = 761
val LOGGER: Logger = LogManager.getLogger("com.github.almightysatan.motdify")

private var port: Int = 25565
private var protocolVersion: Int? = null
private var softwareName = "MOTDify"
private var onlinePlayers: Int = 0
private var maxPlayers: Int = 69
private var favicon: String? = null
private var motd: String = "§7Your favorite p2w server!"
private var disconnectMessage: String = "§cServer not available!"

fun main() {
    lateinit var socket: ServerSocket
    try {
        setIntIfEnvExists("motdify_port", ::port::set)
        setIntIfEnvExists("motdify_protocol_version", ::protocolVersion::set)
        setIfEnvExists("motdify_software", ::softwareName::set)
        setIntIfEnvExists("motdify_players_online", ::onlinePlayers::set)
        setIntIfEnvExists("motdify_players_max", ::maxPlayers::set)
        setFaviconIfEnvExists("motdify_favicon", ::favicon::set)
        setIfEnvExists("motdify_motd") { motd = it.replace("\\n", "\n") }
        setIfEnvExists("motdify_disconnect", ::disconnectMessage::set)

        socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().bind(port = port)
    } catch (t: Throwable) {
        LOGGER.error("Error while initializing", t)
        return
    }

    LOGGER.info("Listening on port $port")

    runBlocking {
        while (true) {
            try {
                val client = socket.accept()
                launch { initConnection(client) }
            } catch (t: Throwable) {
                LOGGER.warn("Error while accepting socket connection", t)
            }
        }
    }
}

fun setIfEnvExists(key: String, setter: (String) -> Unit) {
    if (System.getenv().containsKey(key))
        setter(System.getenv(key))
}

fun setIntIfEnvExists(key: String, setter: (Int) -> Unit) {
    if (System.getenv().containsKey(key))
        setter(System.getenv(key).toInt())
}

fun setFaviconIfEnvExists(key: String, setter: (String?) -> Unit) {
    if (System.getenv().containsKey(key)) {
        val file = File(System.getenv(key))
        if (file.exists()) {
            LOGGER.info("Found favicon ${file.name}")
            setter("data:image/png;base64,${Base64.getEncoder().encodeToString(file.readBytes())}")
        } else
            LOGGER.warn("${file.absolutePath} does not exist!")
    }
}

suspend fun initConnection(socket: Socket) {
    var connection: Connection? = null
    try {
        connection = Connection(socket)
        LOGGER.info("Client connected ${socket.remoteAddress}")
        val handshakePacket = connection.readPacket(PacketServerHandshake())
        connection.protocol = handshakePacket.protocol
        when (handshakePacket.nextState) {
            1 -> handleStatus(connection)
            2 -> handleLogin(connection)
            else -> {
                LOGGER.info("Received invalid state ${handshakePacket.nextState}! Disconnecting client ${socket.remoteAddress}")
                connection.close()
            }
        }
    } catch(t: Throwable) {
        if (connection == null)
            LOGGER.error("Error while initializing connection", t)
        else
            LOGGER.error("Error while communicating with client ${connection.socket.remoteAddress}", t)
    }
}

suspend fun handleStatus(connection: Connection) {
    connection.state = State.STATUS

    connection.readPacket(PacketServerStatus())
    connection.sendPacket(PacketClientStatus(StatusResponse(VersionResponse(softwareName, protocolVersion?: connection.protocol),
        PlayersResponse(maxPlayers, onlinePlayers), TextMessage(motd), favicon)))

    val pingPacket = connection.readPacket(PacketServerPing())
    connection.sendPacket(PacketClientPing(pingPacket.timestamp))
    connection.close()
}

suspend fun handleLogin(connection: Connection) {
    connection.state = State.LOGIN

    val loginPacket = when (connection.protocol) {
        in 0 until ONE_NINETEEN_PROTOCOL -> PacketServerLogin0()
        in ONE_NINETEEN_PROTOCOL until ONE_NINETEEN_THREE_PROTOCOL -> PacketServerLogin759()
        else -> PacketServerLogin761()
    }

    connection.readPacket(loginPacket)
    connection.sendPacket(PacketClientDisconnect(TextMessage(disconnectMessage)))
    connection.close()
}