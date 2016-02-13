package org.mahu.proto.lifecycle.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IRequestProxyList;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ServiceLifeCycleControl implements IServiceLifeCycleControl {

    private final IApiRegistry apiRegistry;
    private final Injector injector;
    private final AbstractServiceModule moduleBindings;
    private final RequestProxyList requestProxyList;
    private final List<ILifeCycleService> startedServices = new LinkedList<>();
    private final List<ILifeCycleService> toBeStoppedServices = new LinkedList<>();
    private final List<ILifeCycleService> stoppedServices = new LinkedList<>();

    public ServiceLifeCycleControl(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        this.apiRegistry = apiRegistry;
        this.moduleBindings = moduleBindings;
        injector = Guice.createInjector(moduleBindings);
        moduleBindings.createLifeCycleServicesObjects(injector);
        requestProxyList = RequestProxyList.class.cast(injector.getInstance(IRequestProxyList.class));
    }

    //@formatter:off
    /**
     * StartServices perform the following tasks:
     * - start all services
     * - enable that requests to services are allowed
     * - make services available in IApiRegistry
     * 
     * Exceptions that happens when a service starts, are not caught 
     * are for the caller of this method to handle.
     */
    //@formatter:on
    @Override
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

    //@formatter:off
    /**
     * StopServices perform the following tasks:
     * - remove services from IApiRegistry
     * - disable processing of requests for services
     * - stop all services
     * - abort received but not completed requests
     * Exceptions that happens when a service starts, are not caught 
     * are for the caller of this method to handle.
     */
    //@formatter:on
    @Override
    public void stopServices() {
        if (!startedServices.isEmpty()) {
            apiRegistry.removeAllPublicServices();
            requestProxyList.rejectExecutionRequests();
            createToBeStoppedServices();
            while (!toBeStoppedServices.isEmpty()) {
                // First stop the first service in queue and if successful (no
                // exception), remove and add to stoppedServiceList.
                final ILifeCycleService service = toBeStoppedServices.get(0);
                service.stop();
                toBeStoppedServices.remove(0);
                stoppedServices.add(service);
            }
            startedServices.clear();
            requestProxyList.abortAllRequests();
        }
    }

    /**
     * abortServices perform the following tasks:
     * - remove services from IApiRegistry
     * - disable processing of requests for service
     * - abort received but not completed requests
     * - stop all services
     */
    @Override
    public void abortServices() {
        if (!startedServices.isEmpty()) {
            apiRegistry.removeAllPublicServices();
            requestProxyList.rejectExecutionRequests();
            requestProxyList.abortAllRequests();
            createToBeStoppedServices();
            startedServices.clear();
            while (!toBeStoppedServices.isEmpty()) {
                final ILifeCycleService service = toBeStoppedServices.get(0);
                service.abort();
                toBeStoppedServices.remove(0);
                stoppedServices.add(service);
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

    public int getStartedServicesCount() {
        return startedServices.size();
    }

    @Override
    public Class<? extends ILifeCycleService> getStartedServiceClass(final int i) {
        return startedServices.get(i).getClass();
    }

    public int getStoppedServicesCount() {
        return stoppedServices.size();
    }
    
    public Class<? extends ILifeCycleService> getStoppedServiceClass(final int i) {
        return stoppedServices.get(i).getClass();
    }

    public int getActiveServiceCount() {
        return getStartedServicesCount() - getStoppedServicesCount();
    }    
}
