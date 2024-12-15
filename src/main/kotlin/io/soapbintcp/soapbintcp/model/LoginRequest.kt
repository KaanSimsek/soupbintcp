package io.soapbintcp.soapbintcp.model

import java.nio.ByteBuffer

/*data class LoginRequest(
    val username: ByteArray = ByteArray(6),
    val password: ByteArray = ByteArray(10),
    val requestedSession: ByteArray = ByteArray(10),
    val requestedSequenceNumber: ByteArray = ByteArray(20)
) {
    fun put(buffer: ByteBuffer) {
        buffer.put(username)
        buffer.put(password)
        buffer.put(requestedSession)
        buffer.put(requestedSequenceNumber)
    }
}*/

data class LoginRequest(
    val username: String,
    val password: String,
    val requestedSession: String,
    val requestedSequenceNumber: String
)

fun ByteBuffer.putLoginRequest(loginRequest: LoginRequest) {
    val username = loginRequest.username.toByteArray(Charsets.US_ASCII).padToSize(6)
    val password = loginRequest.password.toByteArray(Charsets.US_ASCII).padToSize(10)
    val requestedSession = loginRequest.requestedSession.toByteArray(Charsets.US_ASCII).padToSize(10)
    val requestedSequenceNumber = loginRequest.requestedSequenceNumber.toByteArray(Charsets.US_ASCII).padToSize(20)

    this.put(username)
    this.put(password)
    this.put(requestedSession)
    this.put(requestedSequenceNumber)
}

private fun ByteArray.padToSize(size: Int): ByteArray {
    return if (this.size > size) {
        this.copyOf(size)
    } else {
        this.copyOf(size).apply {
            fill(0, this@apply.size - this@padToSize.size)
        }
    }
}

