package io.soapbintcp.soapbintcp.model

data class LoginAccepted(
    val session: String,
    val sequenceNumber: Long
)