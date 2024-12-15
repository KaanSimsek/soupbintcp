package io.soapbintcp.soapbintcp.config

data class SoupBinTcpProperties(
    val username: String,
    val password: String,
    val requestedSession: String,
    val requestedSequenceNumber: Int,
    val url: String,
    val port: Int
)