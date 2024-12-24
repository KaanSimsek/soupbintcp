package io.soapbintcp.soapbintcp.listener

import io.soapbintcp.soapbintcp.model.LoginAccepted
import io.soapbintcp.soapbintcp.model.LoginRejected
import jakarta.xml.bind.JAXBContext
import java.io.StringReader
import java.lang.reflect.Method

class GenericEventListener(
    private val targetBean: Any,
    private val targetMethod: Method
): SoupBinTcpEventListener {
    override fun onSequenceData(message: ByteArray) {
        val messageStr = String(message, Charsets.UTF_8)
        targetMethod.invoke(targetBean, messageStr)
    }

    override fun onLoginAccepted(loginAccepted: LoginAccepted) {
        TODO("Not yet implemented")
    }

    override fun onLoginRejected(loginRejected: LoginRejected) {
        TODO("Not yet implemented")
    }

    override fun onError(e: Exception) {
        TODO("Not yet implemented")
    }
    override fun onDisconnect() {
        TODO("Not yet implemented")
    }
}