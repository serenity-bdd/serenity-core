@file:JvmName("Broadcaster")
package net.serenitybdd.core.eventbus

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object BroadcasterInstance {
    var eventBus: EventBus
    var executorService: ExecutorService

    init {
        executorService = newExecutorService()
        eventBus = newEventBus(executorService)
    }

    fun newEventBus(executorService: ExecutorService) : EventBus {
       return AsyncEventBus("Test lifecycle event bus", executorService)
    }

    fun newExecutorService() : ExecutorService {
        val numberOfCores = Runtime.getRuntime().availableProcessors()
        return Executors.newFixedThreadPool(numberOfCores)
    }

    fun register(subscriber: Any) {
        eventBus.register(subscriber)
    }

    fun shutdown() {
        executorService.shutdown()
        executorService.awaitTermination(30, TimeUnit.SECONDS)

        executorService = newExecutorService()
        eventBus = newEventBus(executorService)
    }
}

// Static method API for Java code
fun register(subscriber : Any) : BroadcasterInstance {
    BroadcasterInstance.register(subscriber)
    return BroadcasterInstance
}

fun postEvent(event : Any) {
    BroadcasterInstance.eventBus.post(event)
}

fun shutdown() {
    BroadcasterInstance.shutdown()
}

