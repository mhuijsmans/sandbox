package org.mahu.proto.restapp.testimpl;

import org.mahu.proto.restapp.model.impl.ProcessDefinitionRepoImpl;

public class ProcessDefinitionRepoExtImpl extends ProcessDefinitionRepoImpl implements ProcessDefinitionRepoExt {

	@Override
	public void clear() {
		super.processes.clear();
	}

}
