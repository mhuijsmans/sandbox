package org.mahu.proto.restapp.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mahu.proto.restapp.model.CloseableTask;
import org.mahu.proto.restapp.model.ProcessTask;

/**
 * Class that can be used as EndTask or it can be extended by a EndTask with
 * logic.
 */
public class ProcessSpawnTask implements CloseableTask, ProcessTask {

	private Process process;
	private List<String> cmdarray;
	private File dir;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		try {
			// spawn process
			// new ProcessBuilder("myCommand", "myArg1", "myArg2");
			ProcessBuilder pb = new ProcessBuilder(cmdarray);
			Map<String, String> env = pb.environment();
			env.put("VAR1", "myValue");
			env.remove("OTHERVAR");
			env.put("VAR2", env.get("VAR1") + "suffix");
			pb.directory(dir);
			//
			process = pb.start();
		} catch (IOException e) {
			throw new ProcessTaskException(e);
		}
		return ProcessTask.Result.Next;
	}

	@Override
	public void close() {
		// close spawned process
		if (process != null) {
			process.destroy();
		}
	}

}