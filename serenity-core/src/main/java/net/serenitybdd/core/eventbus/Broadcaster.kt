@file:JvmName("Broadcaster")
package net.serenitybdd.core.eventbus

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import net.thucydides.core.reports.json.JSONTestOutcomeReader
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object BroadcasterInstance {

    var eventBus: EventBus
    var executorService: ExecutorService

    private val logger = LoggerFactory.getLogger(JSONTestOutcomeReader::class.java)


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
        logger.debug("Broadcaster registered {}", subscriber)
        eventBus.register(subscriber)
    }

    fun post(event: Any) {
        logger.debug("Broadcasting event {}", event)
        eventBus.post(event)
    }

    fun shutdown() {
        logger.debug("Broadcaster shutting down")
        executorService.shutdown()
        executorService.awaitTermination(60, TimeUnit.MINUTES)

        executorService = newExecutorService()
        eventBus = newEventBus(executorService)
    }
}

// Static method API for Java code
/**
 * Register a subscriber to test lifecycle events
 */
fun register(subscriber : Any) : BroadcasterInstance {
    BroadcasterInstance.register(subscriber)
    return BroadcasterInstance
}

fun registerAll(subscribers : Collection<Any>) : BroadcasterInstance {
    for(subscriber in subscribers) {
        register(subscriber)
    }
    return BroadcasterInstance
}

/**
 * Register the default subscribers
 */
fun registerDefaultSubscribers() {
    registerAll(Subscribers.configuredByDefault())
}

fun postEvent(event : Any) {
    BroadcasterInstance.post(event)
}

fun shutdown() {
    BroadcasterInstance.shutdown()
}
