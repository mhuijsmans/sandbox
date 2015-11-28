package org.mahu.proto.restappextra.task;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.Session;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

public class BCChoiceTask implements ProcessTask {

	final static Logger logger = LogManager.getLogger(BCChoiceTask.class.getName());
	
	public final static String BCCHOICEVAR = "bcchoice";
	public final static String QUERY = "query";
	public final static String NOQUERY = "noquery";

	@Inject
	Node node;

	@Inject
	Session session;

	@ProcessTaskResult
	public enum Result {
		QUERY, NOQUERY
	}

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		String var = (String)session.get(BCCHOICEVAR);
		logger.info("BCChoiceTask: node=" + node.getName()+" choice.value="+var);
		return var==null || var.equalsIgnoreCase(NOQUERY) ? Result.NOQUERY : Result.QUERY;
	}

}