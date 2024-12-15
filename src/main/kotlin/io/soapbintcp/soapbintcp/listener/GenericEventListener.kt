package io.soapbintcp.soapbintcp.listener

import io.soapbintcp.soapbintcp.model.LoginAccepted
import io.soapbintcp.soapbintcp.model.LoginRejected
import jakarta.xml.bind.JAXBContext
import java.io.StringReader
import java.lang.reflect.Method

class GenericEventListener<T>(
    private val messageClass: Class<T>,
    private val targetBean: Any,
    private val targetMethod: Method
): SoupBinTcpEventListener {
    override fun onSequenceData(message: ByteArray) {
        val messageStr = String(message, Charsets.UTF_8)
        val jaxbContext = JAXBContext.newInstance(messageClass)
        val unmarshaller = jaxbContext.createUnmarshaller()
        val stringReader = StringReader(messageStr)
        val mappedMessage = unmarshaller.unmarshal(stringReader)
        targetMethod.invoke(targetBean, mappedMessage)
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