package io.soapbintcp.soapbintcp.listener

import io.soapbintcp.soapbintcp.model.LoginAccepted
import io.soapbintcp.soapbintcp.model.LoginRejected

interface SoupBinTcpEventListener {
    fun onSequenceData(message: ByteArray)
    fun onLoginAccepted(loginAccepted: LoginAccepted)
    fun onLoginRejected(loginRejected: LoginRejected)
    fun onError(e: Exception)
    fun onDisconnect()
}