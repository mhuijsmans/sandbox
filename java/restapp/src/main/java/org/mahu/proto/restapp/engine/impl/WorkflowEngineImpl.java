package org.mahu.proto.restapp.engine.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ExecutionErrorException;
import org.mahu.proto.restapp.engine.Job;
import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.ProcessTaskFuture;
import org.mahu.proto.restapp.engine.Session;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.model.CloseableTask;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.NodeRule;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.ProcessTask.ProcessTaskException;

public class WorkflowEngineImpl implements WorkflowEngine {

	protected final static Logger logger = LogManager.getLogger(WorkflowEngine.class.getName());

	/**
	 * A Job may pause, e.g. because it waits for a remote reply. Such a reply
	 * shall be returned within a given time.
	 */
	final static int MAX_WAIT_ON_JOB_IN_MS = 3000;

	private ReentrantLock lock = new ReentrantLock();
	private final TaskCreator taskCreator = new TaskCreator();
	private final PausedTasksListImpl pausedTasks = new PausedTasksListImpl(
			lock);
	private final CloseableTasks closeableTasks = new CloseableTasks();
	protected final Map<String, Object> annotationData = new HashMap<>();

	// For WorkflowEngine2 implementation
	SessionImpl session;
	JobListImpl jobList2;
	SessionId id;
	Listener listener;

	/**
	 * Initializing Object with data for execution.
	 */
	@Override
	public void Init(final SessionId id,
			final ProcessDefinition processDefiniton,
			final Map<String, Object> data, final Listener listener) {
		checkNotNull(id);
		checkNotNull(processDefiniton);
		checkNotNull(data);
		logger.debug("Init() ENTER id=" + id + " process="
				+ processDefiniton.getName());
		//
		this.id = id;
		this.listener = (listener == null) ? new DummyListener() : listener;
		session = new SessionImpl(id, processDefiniton);
		session.copyData(data);
		//
		jobList2 = new JobListImpl(lock);
		session.add(JobList.class.getName(), jobList2);
		session.add(Session.class.getName(), session);
		session.add(SessionId.class.getName(), id);
		//
		session.setStateJobsExists();
		jobList2.add(new JobImpl(session.getSessionId(), session
				.getPresidentProcessDefinition().getFirstNode()));
		session.setStateJobsExists();
		logger.debug("Init() LEAVE");
	}

	/**
	 * Design strategy: the
	 */

	/**
	 * Execute available Jobs
	 * 
	 * @return state which can be EXECUTING_PAUZED, TERMINATED, ABORTED
	 */
	@Override
	public State ExecuteJobs() {
		logger.debug("ExecuteJobs() ENTER id=" + id);
		UpdateState();
		ExecuteJobsWhileStateIsJobsExist();
		logger.debug("ExecuteJobs() LEAVE id=" + id + " state=" + GetState());
		return GetState();
	}

	@Override
	public State ExecuteOneJob() {
		logger.debug("ExecuteOneJob() ENTER id=" + id);
		UpdateState();
		if (session.isStateJobsExists()) {
			ExecuteJob();
		}
		return GetState();
	}

	@Override
	public State ExecuteJobsUntilFinalStateReached(int maxWait)
			throws InterruptedException {
		logger.debug("ExecuteJobsUntilFinalStateReached() ENTER id=" + id);
		UpdateState();
		boolean isTimeout = false;
		while (!isTimeout && session.isStateJobsExistsOrPauzed()) {
			ExecuteJobsWhileStateIsJobsExist();
			switch (GetState()) {
			case TERMINATED:
			case ABORTED:
				CleanUp(this.jobList2);
				break;
			case EXECUTING_PAUZED:
				logger.debug("ExecuteJobsUntilFinalStateReached() PAUZED waiting for jobs id="
						+ id);
				if (jobList2.WaitForJobAvailable(maxWait)) {
					session.setStateJobsExists();
				} else {
					isTimeout = true;
				}
				break;
			default:
				session.setStateReady(new ExecutionErrorException(
						"Implementation error, invalid state=" + GetState()));
				logger.debug("ExecuteJobsUntilFinalStateReached() LEAVE id="
						+ id + " state=ABORTED exception={}", session
						.getException().getMessage());
				CleanUp(this.jobList2);
				break;
			}
		}
		logger.debug("ExecuteJobsUntilFinalStateReached() ENTER id=" + id
				+ " state=" + GetState());
		return GetState();
	}

	private void ExecuteJobsWhileStateIsJobsExist() {
		while (session.isStateJobsExists()) {
			ExecuteJob();
		}
	}

	private State ExecuteJob() {
		logger.debug("ExecuteJob() ENTER id=" + id);
		Job job = jobList2.getJob();
		if (job != null) {
			Node node = job.getNode();
			PausableTask task = job.getProcessTask();
			// Set the current executing node in the session
			session.add(Node.class.getName(), node);
			//
			node = executeNode(session, job.getProcessDefinition(), node, task,
					jobList2);
			if (session.isStateAborted()) {
				// executeNode resulted in error
			} else {
				if (node != null) {
					// there is a next node. ensure that it is executed
					// first by inserting it to the top
					jobList2.addFirst(new JobImpl(session.getSessionId(), node));
				} else {
					// There is no next node.
					UpdateState();
				}
			}
		} else {
			session.setStateReady(new ExecutionErrorException(
					"ExecuteJob, implementation error job not found"));
		}
		switch (GetState()) {
		case TERMINATED:
			logger.debug("ExecuteJob() LEAVE id=" + id + " state=TERMINATED");
			break;
		case ABORTED:
			logger.debug("ExecuteJob() LEAVE id=" + id
					+ " state=ABORTED exception={}", session.getException()
					.getMessage());
			break;
		case EXECUTING_JOBS_EXIST:
			logger.debug("ExecuteJob() LEAVE id=" + id
					+ " state=EXECUTING_JOBS_EXIST");
			break;
		case EXECUTING_PAUZED:
			logger.debug("ExecuteJob() LEAVE id=" + id
					+ " state=EXECUTING_PAUZED");
			break;
		default:
			session.setStateReady(new ExecutionErrorException(
					"Implementation error, unknown state"));
			logger.debug("ExecuteJob() LEAVE id=" + id
					+ " state=ABORTED exception={}", session.getException()
					.getMessage());
			break;

		}
		return GetState();
	}

	@Override
	public State GetState() {
		return session.getState();
	}

	private void UpdateState() {
		// Perhaps there are new jobs added or paused tasks created.
		// One synchronized block to prevent race condition,
		// where paused task moved from pausedTasks to JobList.
		if (!session.isStateReady()) {
			synchronized (lock) {
				if (jobList2.size() > 0) {
					session.setStateJobsExists();
				} else if (pausedTasks.size() > 0) {
					session.setStatePauzed();
				} else {
					session.setStateReady();
				}
			}
		}
	}

	/**
	 * Execute the ProcessTask of a Node.
	 * 
	 * @param session
	 * @param processDefinition
	 * @param node
	 * @param pausableTask
	 * @return
	 */
	private Node executeNode(final SessionImpl session,
			final ProcessDefinition processDefinition, final Node node,
			PausableTask pausableTask, final JobList joblist) {
		logger.debug("executeNode() ENTER node="
				+ node.getName()
				+ ", task="
				+ (pausableTask != null ? pausableTask.getClass().getName()
						: "<none>"));
		ProcessTask task = null;
		if (pausableTask == null) {
			task = taskCreator.createTaskInstance(session, node);
			if (session.isStateJobsExistsOrPauzed()) {
				if (task instanceof PausableTask) {
					ProcessTaskFuture future = new ProcessTaskFutureImpl(
							session.getSessionId(), node, (PausableTask) task,
							joblist, listener);
					annotationData.put(ProcessTaskFuture.class.getName(),
							future);
				}
				AnnotationProcessor.AnnotateInstance(session, node, task,
						annotationData);
			}
		} else {
			// If this was a paused task, it needs to be removed from
			// pausedTasks
			pausedTasks.remove(pausableTask);
			if (pausableTask instanceof ProcessTask) {
				task = (ProcessTask) pausableTask;
			} else {
				// This is a serious fault
				String errorMsg = "ProcessTaskImpl implements PausableProcessTask but not ProcessTask: "
						+ pausableTask.getClass().getName();
				logger.error(errorMsg);
				session.setStateReady(new ExecutionErrorException(errorMsg));
			}
		}
		Enum<?> result = null;
		if (session.isStateJobsExistsOrPauzed()) {
			result = executeProcessTask(session, processDefinition, node, task);
		}
		Node nextNode = null;
		// Another state check, because the executeProcesstask may have resulted
		// in state change.
		if (session.isStateJobsExistsOrPauzed()) {
			if (result == PausableTask.Result.Pauze) {
				// Task requested pause, so it must implements the
				// PausableProcessTask interface
				if (task instanceof PausableTask) {
					pausedTasks.add((PausableTask) task);
					// If processTask paused, it will continue at a point later
					// in time.
					// Right now there is no next node.
				} else {
					// This is a application fault.
					String errorMsg = "ProcessTaskImpl returned "
							+ PausableTask.Result.Pauze.toString()
							+ " but does not implement inteface: "
							+ task.getClass().getName();
					logger.error(errorMsg);
					session.setStateReady(new ExecutionErrorException(errorMsg));
				}
			} else {
				if (task instanceof CloseableTask) {
					closeableTasks.add((CloseableTask) task);
				}
				nextNode = determineNextNode(session, processDefinition, node,
						result);
			}
		}
		logger.debug("executeNode() LEAVE");
		return nextNode;
	}

	private void CleanUp(JobListImpl jobList) {
		// The outcome of these tasks doesn't impact the session outcome
		closeableTasks.callCloseOnTasksAndClear();
		pausedTasks.cancelTasksAndCLear();
		jobList.clear();
	}

	/**
	 * Execute a processTask. Catching application level exceptions. If
	 * exception is caught, set state to Ready.
	 * 
	 * @param session
	 * @param processDefinition
	 * @param node
	 * @param task
	 * @return
	 */
	private Enum<?> executeProcessTask(final SessionImpl session,
			final ProcessDefinition processDefinition, final Node node,
			final ProcessTask task) {
		Enum<?> result = ProcessTask.Result.Null;
		try {
			logger.debug("Executing process={} node={} task.class={}",
					processDefinition.getName(), node.getName(), task
							.getClass().getName());
			result = task.execute();
		} catch (ProcessTaskException e) {
			logger.debug(
					"ProcessTaskException process={} node={} task.class={} ProcessTaskException.message={}",
					processDefinition.getName(), node.getName(), task
							.getClass().getName(), e.getMessage());
			session.setStateReady(e);
		} catch (RuntimeException e) {
			logger.debug(
					"Aborted process {} task {} class {} RuntimeException.message {}",
					processDefinition.getName(), node.getName(), task
							.getClass().getName(), e.getMessage());
			session.setStateReady(new UncaughtException(
					"Uncaught exception in node: " + node.getName()
							+ " in ProcessTask: " + task.getClass().getName(),
					e));
		}
		// TODO: complete the code below.
		// Certain exceptions are not caught, e.g. ThreadInterrupted.
		return result;
	}

	private Node determineNextNode(final SessionImpl session,
			final ProcessDefinition processDefinition, final Node node,
			final Enum<?> result) {
		Node nextNode = null;
		if (node.getNrOfNodeRules() == 0) {
			// Special case: ForkTask, which has created new Job's to execute
			// for all fork's.
			if (result == ForkTask.Result.Fork) {
				// It must return null, to indicate that path "ending" with work
				// has completed.
				;
			} else if (result == ProcessTask.Result.Null) {
				// Successfully completed execution
				if (processDefinition.isPresident()) {
					session.setStateReady();
				}
				// else a non-president path has completed
				// (fork(join)-processDefinition).
			} else {
				session.setStateReady(new ExecutionErrorException(
						"Invalid result, expected null, got " + result));
			}
		} else {
			// Non-final task, check result against specified results
			NodeRule rule = node.findRule(result);
			if (rule == null) {
				noRuleFoundSetStateReady(session, node, result);
			} else {
				String nameNextNode = rule.getNameNextTask();
				nextNode = processDefinition.getNode(nameNextNode);
				if (nextNode == null) {
					nextNodeNotFoundSetStateReady(session, processDefinition,
							nameNextNode);
				}
			}
		}
		return nextNode;
	}

	private void nextNodeNotFoundSetStateReady(final SessionImpl session,
			final ProcessDefinition processDefinition, final String nameNextNode) {
		session.setStateReady(new ExecutionErrorException(
		// internal error, because invalid ProcessDefinition has been excepted.
				"Internal error, in processDef " + processDefinition.getName()
						+ " name next node not found " + nameNextNode));
	}

	private void noRuleFoundSetStateReady(final SessionImpl session,
			final Node node, final Enum<?> result) {
		if (node.getNrOfResults() > 0) {
			session.setStateReady(new ExecutionErrorException("Node "
					+ node.getName()
					+ ", invalid result, got non-annotated result " + result));
		} else {
			session.setStateReady(new ExecutionErrorException("Node "
					+ node.getName() + ", returned result " + result
					+ " but specified " + node.getNodeRule(0).getValue()));
		}
	}

	static class DummyListener implements Listener {

		@Override
		public void ProcessTaskFutureHasCompleted(SessionId id) {
		}

	}

}
