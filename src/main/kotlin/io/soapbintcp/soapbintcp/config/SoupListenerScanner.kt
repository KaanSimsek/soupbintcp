package io.soapbintcp.soapbintcp.config

import io.soapbintcp.soapbintcp.annotation.EnableSoupListener
import io.soapbintcp.soapbintcp.annotation.SoupListener
import io.soapbintcp.soapbintcp.client.SoapBinTcpClient
import io.soapbintcp.soapbintcp.listener.GenericEventListener
import io.soapbintcp.soapbintcp.model.LoginRequest
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

@EnableConfigurationProperties(MultipleSocketConnectionProperties::class)
@Component
class SoupListenerScanner(
    private val multipleSocketConnectionProperties: MultipleSocketConnectionProperties
): BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String) {
        if (bean.javaClass.isAnnotationPresent(EnableSoupListener::class.java)) {
            bean.javaClass.declaredMethods.forEach { method ->
                if (method.isAnnotationPresent(SoupListener::class.java)) {
                    val annotation = method.getAnnotation(SoupListener::class.java)
                    val messageClass = method.parameters[0].type
                    createAndStartClient(method, bean, messageClass, annotation.topic)
                }
            }
        }
    }

    private fun createAndStartClient(
        method: Method,
        bean: Any,
        messageClass: Class<*>,
        topic: String
    ) {
        val soupBinTcpProperties = multipleSocketConnectionProperties.connections[topic] ?:
        throw RuntimeException("Topic named $topic not found") // Todo: Throw custom exception and kill the app

        val loginRequest = LoginRequest(
            username = soupBinTcpProperties.username,
            password = soupBinTcpProperties.password,
            requestedSession = soupBinTcpProperties.requestedSession,
            requestedSequenceNumber = soupBinTcpProperties.requestedSequenceNumber.toString() // Todo: Burayı düşün
        )

        val eventListener = GenericEventListener(
            messageClass = messageClass,
            targetMethod = method,
            targetBean = bean
        )

        val socketChannel = SocketChannel.open()
        socketChannel.connect(InetSocketAddress(soupBinTcpProperties.url, soupBinTcpProperties.port))
        val client = SoapBinTcpClient(listener = eventListener, socket = socketChannel)
        client.login(loginRequest)
        Thread {
            while (true) {
                client.listen()
            }
        }.start()
    }
}