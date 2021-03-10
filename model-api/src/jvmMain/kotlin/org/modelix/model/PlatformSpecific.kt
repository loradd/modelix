package org.modelix.model

import org.apache.commons.collections4.map.LRUMap
import org.apache.log4j.Logger
import java.util.*
import kotlin.reflect.KClass


actual fun logError(message: String, exception: Exception, contextClass: KClass<*>) {
    Logger.getLogger(contextClass.java).error(message, exception)
}

actual fun logWarning(message: String, exception: Exception, contextClass: KClass<*>) {
    Logger.getLogger(contextClass.java).warn(message, exception)
}

actual fun logDebug(message: () -> String?, contextClass: KClass<*>) {
    val logger = Logger.getLogger(contextClass.java)
    if (logger.isDebugEnabled) {
        val msg = message()
        if (!msg.isNullOrEmpty()) logger.debug(msg)
    }
}

actual fun logTrace(message: () -> String?, contextClass: KClass<*>) {
    val logger = Logger.getLogger(contextClass.java)
    if (logger.isTraceEnabled) {
        val msg = message()
        if (!msg.isNullOrEmpty()) logger.trace(msg)
    }
}

actual inline fun <R> runSynchronized(lock: Any, block: () -> R): R {
    return synchronized(lock, block)
}