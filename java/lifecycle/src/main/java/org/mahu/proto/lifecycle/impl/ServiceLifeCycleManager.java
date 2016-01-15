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
    private final List<ILifeCycleService> toBeStoppedServices = new LinkedList<>();

    public ServiceLifeCycleManager(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        this.apiRegistry = apiRegistry;
        this.moduleBindings = moduleBindings;
        injector = Guice.createInjector(moduleBindings);
        moduleBindings.createLifeCycleServicesObjects(injector);
        requestProxyList = RequestProxyList.class.cast(injector.getInstance(IRequestProxyList.class));
    }

    /**
     * StartServices perform the following tasks:
     * 
     * - start all services
     * 
     * - enable that requests to services are allowed
     * 
     * - make services available in IApiRegistry
     */
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
        }
    }

    /**
     * StopServices perform the following tasks:
     * 
     * - remove services from IApiRegistry
     * 
     * - disable processing of requests for services
     * 
     * - stop all services
     * 
     * - abort received but not completed requests
     */
    public void stopServices() {
        if (!startedServices.isEmpty()) {
            apiRegistry.removeAllPublicServices();
            requestProxyList.rejectExecutionRequests();
            createToBeStoppedServices();
            while (!toBeStoppedServices.isEmpty()) {
                toBeStoppedServices.get(0).stop();
                toBeStoppedServices.remove(0);
            }
            startedServices.clear();
            requestProxyList.abortAllRequests();
        }
    }

    /**
     * abortServices perform the following tasks:
     * 
     * - remove services from IApiRegistry
     * 
     * - disable processing of requests for service
     * 
     * - abort received but not completed requests
     * 
     * - stop all services
     */    
    public void abortServices() {
        if (!startedServices.isEmpty()) {
            apiRegistry.removeAllPublicServices();
            requestProxyList.rejectExecutionRequests();
            requestProxyList.abortAllRequests();
            createToBeStoppedServices();
            startedServices.clear();
            while (!toBeStoppedServices.isEmpty()) {
                toBeStoppedServices.get(0).abort();
                toBeStoppedServices.remove(0);
            }
        }
    }

    public void createToBeStoppedServices() {
        if (toBeStoppedServices.isEmpty()) {
            List<ILifeCycleService> tmp = new LinkedList<>(startedServices);
            Collections.reverse(tmp);
            toBeStoppedServices.addAll(tmp);
        }
    }
}
