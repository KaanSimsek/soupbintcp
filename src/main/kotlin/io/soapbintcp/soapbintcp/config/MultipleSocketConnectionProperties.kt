package io.soapbintcp.soapbintcp.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "soap-socket")
data class MultipleSocketConnectionProperties(
    val connections: Map<String, SoupBinTcpProperties>
)
