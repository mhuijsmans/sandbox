package org.mahu.proto.lifecycle.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IRequestProxyList;
import org.mahu.proto.lifecycle.IServiceLifeCycleManager;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ServiceLifeCycleManager implements IServiceLifeCycleManager {

    private final IApiRegistry apiRegistry;
    private final Injector injector;
    private final AbstractServiceModule moduleBindings;
    private final RequestProxyList requestProxyList;
    private final List<ILifeCycleService> startedServices = new LinkedList<>();

    public ServiceLifeCycleManager(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        this.apiRegistry = apiRegistry;
        this.moduleBindings = moduleBindings;
        injector = Guice.createInjector(moduleBindings);
        moduleBindings.createLifeCycleServicesObjects(injector);
        requestProxyList = RequestProxyList.class.cast(injector.getInstance(IRequestProxyList.class));
    }

    public void startServices() {
        if (startedServices.isEmpty()) {
            final Iterator<ILifeCycleService> it = moduleBindings.getObjectRegistry().getLifeCycleServiceIterator();
            while (it.hasNext()) {
                final ILifeCycleService service = it.next();
                startedServices.add(service);
                service.start();
            }
            requestProxyList.allowedExecutionRequests();
            apiRegistry.setPublicService(moduleBindings.getObjectRegistry().getPublicServiceKeys());
        } else {
            throw new RuntimeException("Called twice");
        }
    }

    public void stopServices() {
        apiRegistry.removeAllPublicServices();
        requestProxyList.rejectExecutionRequests();
        final Iterator<ILifeCycleService> it = getStartedServicesReverseIterator();
        boolean noStopError = true;
        while (it.hasNext()) {
            final ILifeCycleService service = it.next();
            if (noStopError) {
                noStopError = service.stop();
                if (!noStopError) {
                    service.abort();
                }
            } else {
                service.abort();
            }
        }
        startedServices.clear();
        requestProxyList.abortAllRequests();
    }

    public void abortServices() {
        requestProxyList.rejectExecutionRequests();        
        requestProxyList.abortAllRequests();
        apiRegistry.removeAllPublicServices();
        final Iterator<ILifeCycleService> it = getStartedServicesReverseIterator();
        while (it.hasNext()) {
            it.next().abort();
        }
        startedServices.clear();
    }

    public Iterator<ILifeCycleService> getStartedServicesReverseIterator() {
        List<ILifeCycleService> tmp = new LinkedList<>(startedServices);
        Collections.reverse(tmp);
        return tmp.iterator();
    }
}
