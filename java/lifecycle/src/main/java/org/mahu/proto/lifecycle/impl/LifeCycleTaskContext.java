package org.mahu.proto.lifecycle.impl;

import java.util.Optional;

import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;

class LifeCycleTaskContext {
    private final IApiRegistry apiRegistry;
    private final AbstractServiceModule moduleBindings;
    private final LifeCycleManagerUncaughtExceptionHandler uncaughtExceptionHandler;
    private Optional<IServiceLifeCycleControl> serviceLifeCycleControl;
    private final LifeCycleManagerStatus status;
    private final LifeCycleManagerExecutorService executorService;

    LifeCycleTaskContext(final IApiRegistry apiRegistry, final AbstractServiceModule moduleBindings) {
        this.apiRegistry = apiRegistry;
        this.moduleBindings = moduleBindings;
        // TODO: mutual dependency LifeCycleManagerUncaughtExceptionHandler <->
        // LifeCycleTaskContext
        this.uncaughtExceptionHandler = new LifeCycleManagerUncaughtExceptionHandler(this);
        status = new LifeCycleManagerStatus();
        executorService = new LifeCycleManagerExecutorService();
        // TODO: another mutual dependency
        executorService.init(uncaughtExceptionHandler);
        serviceLifeCycleControl = Optional.empty();
    }

    LifeCycleManagerStatus getStatus() {
        return status;
    }

    LifeCycleManagerExecutorService getExecutorService() {
        return executorService;
    }

    AbstractServiceModule getModuleBindings() {
        return moduleBindings;
    }

    LifeCycleManagerUncaughtExceptionHandler getLifeCycleManagerUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    void createNewServiceLifeCycleControl() {
        final ServicesLifeCycleControlUncaughtExceptionHandler uncaughtExceptionHandler = new ServicesLifeCycleControlUncaughtExceptionHandler(
                getLifeCycleManagerUncaughtExceptionHandler(), getStatus().getServicesStartCount());
        getModuleBindings().setUncaughtExceptionHandler(uncaughtExceptionHandler);
        serviceLifeCycleControl = Optional.of(new ServiceLifeCycleControl(apiRegistry, moduleBindings));
    }

    public Optional<IServiceLifeCycleControl> getServiceLifeCycleControl() {
        return serviceLifeCycleControl;
    }
}
