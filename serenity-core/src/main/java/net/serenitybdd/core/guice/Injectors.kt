package net.serenitybdd.core.guice

import net.thucydides.core.guice.Injectors

fun <T> injected(clazz : Class<T>) : T {
    return Injectors.getInjector().getInstance(clazz)
}