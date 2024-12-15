package io.soapbintcp.soapbintcp.model

object SoapBinTcpPacket {

    /*
     * These messages can be sent by both the client and server.
     */
    const val PACKET_TYPE_DEBUG: Byte = '+'.code.toByte()

    /*
     * These messages are sent by the server to the client.
     */
    const val PACKET_TYPE_LOGIN_ACCEPTED: Byte = 'A'.code.toByte()
    const val PACKET_TYPE_LOGIN_REJECTED: Byte = 'J'.code.toByte()
    const val PACKET_TYPE_SEQUENCED_DATA: Byte = 'S'.code.toByte()
    const val PACKET_TYPE_SERVER_HEARTBEAT: Byte = 'H'.code.toByte()
    const val PACKET_TYPE_END_OF_SESSION: Byte = 'Z'.code.toByte()

    /*
     * These messages are sent by the client to the server.
     */
    const val PACKET_TYPE_LOGIN_REQUEST: Byte = 'L'.code.toByte()
    const val PACKET_TYPE_UNSEQUENCED_DATA: Byte = 'U'.code.toByte()
    const val PACKET_TYPE_CLIENT_HEARTBEAT: Byte = 'R'.code.toByte()
    const val PACKET_TYPE_LOGOUT_REQUEST: Byte = 'O'.code.toByte()

    const val MAX_PACKET_LENGTH: Int = 65535

    /**
     * The login reject code Not Authorized.
     */
    const val LOGIN_REJECT_CODE_NOT_AUTHORIZED: Byte = 'A'.code.toByte()

    /**
     * The login reject code Session Not Available.
     */
    const val LOGIN_REJECT_CODE_SESSION_NOT_AVAILABLE: Byte = 'S'.code.toByte()

    const val RX_HEARTBEAT_TIMEOUT_MILLIS: Long = 15000
    const val TX_HEARTBEAT_INTERVAL_MILLIS: Long = 1000
    const val PACKET_HEADER_LENGTH: Int = 3

}