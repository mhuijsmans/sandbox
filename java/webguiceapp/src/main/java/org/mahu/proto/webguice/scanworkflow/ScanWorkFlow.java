package org.mahu.proto.webguice.scanworkflow;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mahu.proto.webguice.workstep.Step;
import org.mahu.proto.webguice.worktask.Task2Data;

public class ScanWorkFlow {

    private final Provider<Step> stepProvider;

    @Inject
    ScanWorkFlow(final Provider<Step> stepProvider) {
        this.stepProvider = stepProvider;
    }

    public void execute() {
        Task2Data data = new Task2Data();
        stepProvider.get().execute(data);
    }

}
