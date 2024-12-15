package io.soapbintcp.soapbintcp.annotation



@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SoupListener(val topic: String)
