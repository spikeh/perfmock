package uk.davidwei.perfmock.lib;

import uk.davidwei.perfmock.api.Imposteriser;
import uk.davidwei.perfmock.api.Invocation;
import uk.davidwei.perfmock.api.Invokable;
import uk.davidwei.perfmock.internal.SearchingClassLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * An {@link uk.davidwei.perfmock.api.Imposteriser} that uses the
 * {@link java.lang.reflect.Proxy} class of the Java Reflection API.
 * 
 * @author npryce
 *
 */
public class JavaReflectionImposteriser implements Imposteriser {
    public static final Imposteriser INSTANCE = new JavaReflectionImposteriser();
    
    public boolean canImposterise(Class<?> type) {
        return type.isInterface();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T imposterise(final Invokable mockObject, Class<T> mockedType, Class<?>... ancilliaryTypes) {
        final Class<?>[] proxiedClasses = prepend(mockedType, ancilliaryTypes);
        final ClassLoader classLoader = SearchingClassLoader.combineLoadersOf(proxiedClasses);
        
        return (T)Proxy.newProxyInstance(classLoader, proxiedClasses, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return mockObject.invoke(new Invocation(proxy, method, args));
            }
        });
    }
    
    private Class<?>[] prepend(Class<?> first, Class<?>... rest) {
        Class<?>[] proxiedClasses = new Class<?>[rest.length+1];
        
        proxiedClasses[0] = first;
        System.arraycopy(rest, 0, proxiedClasses, 1, rest.length);
        
        return proxiedClasses;
    }
}
