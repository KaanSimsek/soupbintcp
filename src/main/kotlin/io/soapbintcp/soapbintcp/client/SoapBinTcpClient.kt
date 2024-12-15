package io.soapbintcp.soapbintcp.client

import io.soapbintcp.soapbintcp.listener.SoupBinTcpEventListener
import io.soapbintcp.soapbintcp.model.LoginRequest
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.MAX_PACKET_LENGTH
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_HEADER_LENGTH
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_CLIENT_HEARTBEAT
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_LOGIN_ACCEPTED
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_LOGIN_REJECTED
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_LOGIN_REQUEST
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_LOGOUT_REQUEST
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.PACKET_TYPE_SEQUENCED_DATA
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.RX_HEARTBEAT_TIMEOUT_MILLIS
import io.soapbintcp.soapbintcp.model.SoapBinTcpPacket.TX_HEARTBEAT_INTERVAL_MILLIS
import io.soapbintcp.soapbintcp.model.putLoginRequest
import jakarta.annotation.PreDestroy
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SocketChannel
import java.time.Instant
import kotlin.concurrent.Volatile
import kotlin.math.min

class SoapBinTcpClient(
    val listener: SoupBinTcpEventListener,
    val socket: SocketChannel
) {
    /*
     * This variable is written on data reception and read on session
     * keep-alive. These two functions can run on different threads
     * without locking.
     */
    @Volatile
    private var lastReceiveMillis: Long = Instant.now().toEpochMilli()
    /*
     * This variable is written on data transmission and read on session
     * keep-alive. These two functions can run on different threads but
     * require locking.
     */
    private var lastTransactionMillis: Long = Instant.now().toEpochMilli()

    private val txHeader: ByteBuffer = ByteBuffer.allocateDirect(PACKET_HEADER_LENGTH)
    private val txPayload = ByteBuffer.allocateDirect(46)
    private val txBuffers = arrayOfNulls<ByteBuffer>(2)

    private val rxBuffer = ByteBuffer.allocateDirect(
        PACKET_HEADER_LENGTH + min(
            MAX_PACKET_LENGTH - 1,
            MAX_PACKET_LENGTH - 1
        )
    )

    init {
        txHeader.order(ByteOrder.BIG_ENDIAN)
        txBuffers[0] = txHeader
    }

    fun login(loginRequest: LoginRequest) {
        txPayload.clear()
        txPayload.putLoginRequest(loginRequest)
        txPayload.flip()
        send(PACKET_TYPE_LOGIN_REQUEST, txPayload)
    }

    @PreDestroy
    fun logout() {
        send(PACKET_TYPE_LOGOUT_REQUEST)
    }

    fun listen(): Int { // Todo make the login request long polling.
        val bytes = socket.read(rxBuffer)
        if (bytes <= 0) return bytes
        rxBuffer.flip()
        while (parse()) {}
        rxBuffer.compact()
        // Todo: Set millie seconds for received data
        return bytes
    }

    fun handleHeartbeat() {
        val currentTimeMillis = Instant.now().toEpochMilli()
        if (currentTimeMillis - lastTransactionMillis > TX_HEARTBEAT_INTERVAL_MILLIS) {
            send(PACKET_TYPE_CLIENT_HEARTBEAT)
        }
        if (currentTimeMillis - lastReceiveMillis > RX_HEARTBEAT_TIMEOUT_MILLIS) {
            // Todo: Handle exception and reconnection to socket server
        }
    }

    private fun send(packetType: Byte, payload: ByteBuffer) {
        val packetLength = payload.remaining() + 1
        // Todo: Add custom exception
        if (packetLength > MAX_PACKET_LENGTH) throw RuntimeException("Packet length is too large")
        txHeader.clear()
        txHeader.putShort(packetLength.toShort())
        txHeader.put(packetType)
        txHeader.flip()
        txBuffers[1] = payload
        socket.write(txBuffers)

        // Todo: Set last transaction millies
    }

    private fun send(packetType: Byte) {
        txHeader.clear()
        txHeader.putShort(1)
        txHeader.put(packetType)
        txHeader.flip()
        socket.write(txBuffers)
        // Todo: Set last transaction millie
    }

    private fun parse(): Boolean {
        if (rxBuffer.remaining() < 2) return false

        rxBuffer.mark()

        rxBuffer.order(ByteOrder.BIG_ENDIAN)

        val packetLength = rxBuffer.getShort().toInt() and '\uffff'.code

        if (packetLength > rxBuffer.capacity() - 2) throw Exception("Packet length exceeds buffer capacity")

        if (rxBuffer.remaining() < packetLength) {
            rxBuffer.reset()
            return false
        }

        val packetType = rxBuffer.get()

        val limit = rxBuffer.limit()

        rxBuffer.limit(rxBuffer.position() + packetLength - 1)

        handlePayloadByPacketType(packetType, rxBuffer)

        rxBuffer.position(rxBuffer.limit())
        rxBuffer.limit(limit)

        return true
    }

    private fun handlePayloadByPacketType(packetType: Byte, payload: ByteBuffer) {
        when (packetType) {
            PACKET_TYPE_LOGIN_ACCEPTED -> {
                //listener.onLoginAccepted()
            }
            PACKET_TYPE_LOGIN_REJECTED -> {
                //listener.onLoginRejected()
            }
            PACKET_TYPE_SEQUENCED_DATA -> {
                val bytes = ByteArray(payload.remaining())
                payload.get(bytes)
                listener.onSequenceData(bytes)
            }
        }
    }
}