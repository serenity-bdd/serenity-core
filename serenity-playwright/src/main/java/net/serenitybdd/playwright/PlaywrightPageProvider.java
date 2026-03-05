package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.serenitybdd.model.steps.StepParameterProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * SPI provider that resolves Playwright {@link Page} constructor parameters
 * for step libraries created via {@code @Steps}.
 * <p>
 * Instead of supplying the real Page (which may not exist yet at injection time),
 * this provider creates a lazy proxy that delegates to
 * {@link PlaywrightSerenity#getCurrentPage()} on every method call. This means:
 * <ul>
 *   <li>Step libraries can be injected before {@code @BeforeEach} runs</li>
 *   <li>Shared step libraries ({@code @Steps(shared=true)}) automatically see
 *       the current test's Page via the thread-local registry</li>
 *   <li>Constructors that eagerly resolve locators (e.g. {@code page.getByLabel(...)})
 *       work because interface return types are automatically wrapped in deferred
 *       proxies that resolve when a terminal method is finally called</li>
 * </ul>
 */
public class PlaywrightPageProvider implements StepParameterProvider {

    @Override
    public Optional<Object> resolve(Class<?> type) {
        if (type == Page.class) {
            return Optional.of(createLazyPageProxy());
        }
        return Optional.empty();
    }

    private Page createLazyPageProxy() {
        return (Page) createDeferringProxy(Page.class, () -> {
            Page realPage = PlaywrightSerenity.getCurrentPage();
            if (realPage == null) {
                throw new PageNotYetAvailableException();
            }
            return realPage;
        });
    }

    /**
     * Creates a proxy that defers method calls until the real object is available.
     * <p>
     * When the real object is available (supplier succeeds), all method calls delegate directly.
     * When not yet available, methods that return an interface type get a deferred sub-proxy
     * so that locator chains like {@code page.getByLabel("Name")} can be stored as fields
     * during construction and resolved later when the real Page becomes available.
     */
    private static Object createDeferringProxy(Class<?> interfaceType, Supplier<Object> realObjectSupplier) {
        return Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new DeferringInvocationHandler(realObjectSupplier)
        );
    }

    private static class DeferringInvocationHandler implements InvocationHandler {
        private final Supplier<Object> realObjectSupplier;

        DeferringInvocationHandler(Supplier<Object> realObjectSupplier) {
            this.realObjectSupplier = realObjectSupplier;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Handle Object methods on the proxy itself
            if ("toString".equals(method.getName()) && method.getParameterCount() == 0) {
                return "DeferringProxy[" + method.getDeclaringClass().getSimpleName() + "]";
            }
            if ("hashCode".equals(method.getName()) && method.getParameterCount() == 0) {
                return System.identityHashCode(proxy);
            }
            if ("equals".equals(method.getName()) && method.getParameterCount() == 1) {
                return proxy == args[0];
            }

            // Try to resolve the real object
            Object realObject;
            try {
                realObject = realObjectSupplier.get();
            } catch (PageNotYetAvailableException e) {
                // Real object not available yet — if the return type is an interface,
                // return a deferred sub-proxy so locator chains can be stored as fields.
                Class<?> returnType = method.getReturnType();
                if (returnType.isInterface()) {
                    return createDeferringProxy(returnType, () -> {
                        try {
                            Object resolved = realObjectSupplier.get();
                            return method.invoke(resolved, args);
                        } catch (InvocationTargetException ex) {
                            Throwable cause = ex.getCause();
                            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                            if (cause instanceof Error) throw (Error) cause;
                            throw new RuntimeException(cause);
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
                throw new IllegalStateException(
                        "No Playwright Page available. Register a page via "
                                + "PlaywrightSerenity.registerPage() or @UsePlaywright "
                                + "before calling step methods.");
            }

            // Real object is available — delegate directly
            try {
                return method.invoke(realObject, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    /**
     * Internal marker exception used to distinguish "page not yet available"
     * from other failures, so the deferring handler knows when to create sub-proxies.
     */
    static class PageNotYetAvailableException extends RuntimeException {
        PageNotYetAvailableException() {
            super("Playwright Page not yet available");
        }
    }
}
