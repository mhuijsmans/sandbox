package org.mahu.proto.restapp.xmladapter;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessPathBuilder;
import org.mahu.proto.restapp.task.EndTask;
import org.mahu.proto.restapp.util.NotImplementedException;
import org.mahu.proto.restapp.util.ReflectionUtil;
import org.mahu.proto.restapp.util.ReflectionUtil.ReflectionUtilException;

public class ActivitiBmpn2XmlReader {

	private enum Element {
		SERVICETASK, STARTEVENT, ENDEVENT, PROCESS, SEQUENCEFLOW, PARALLELGATEWAY
	}

	private enum PeType {
		OTHER, FORK, JOIN
	}

	private final XMLInputFactory factory;
	private final Map<String, Element> nameToTypeMapping = new HashMap<>();

	class ProcessElement {
		private final Element element;
		private PeType peType = PeType.OTHER;

		private final Map<String, String> attributes = new HashMap<>();

		ProcessElement(final Element element) {
			this.element = element;
			System.out.println("ProcessElement: " + element);
		}

		void addAttribute(String name, String value) {
			System.out.println("ProcessElement: " + element
					+ " [attr,value]==[" + name + "," + value + "]");
			attributes.put(name, value);
		}

		// Id serves as identification for an element. It is mandatory.
		// It is a system generated and shall not be changed by the user.
		String getId() {
			return getAttribute("id");
		}

		// Logical id assigned by the implementor.
		String getName() {
			String name = getAttribute("name");
			return name;
		}

		String getSourceRef() {
			return getAttribute("sourceRef");
		}

		String getTargetRef() {
			return getAttribute("targetRef");
		}

		String getClassName() {
			return getAttribute("class");
		}

		String getAttribute(String name) {
			return attributes.get(name);
		}

		void SetTypeFork() {
			assert peType == PeType.OTHER : "PeType already set to: " + peType;
			peType = PeType.FORK;
		}

		void SetTypeJoin() {
			assert peType == PeType.OTHER : "PeType already set to: " + peType;
			peType = PeType.JOIN;
		}

		boolean IsFork() {
			return element == Element.PARALLELGATEWAY
					&& GetPyType() == PeType.FORK;
		}

		boolean IsJoin() {
			return element == Element.PARALLELGATEWAY
					&& GetPyType() == PeType.JOIN;
		}

		boolean IsServiceTask() {
			return element == Element.SERVICETASK;
		}

		boolean IsEndEvent() {
			return element == Element.ENDEVENT;
		}

		PeType GetPyType() {
			return peType;
		}

		ProcessElement GetServiceTask() {
			assert element == Element.SEQUENCEFLOW;
			return getElement(Element.SERVICETASK, new AttributeMatcher("id",
					getId()));
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ProcessElement[").append("element=").append(element);
			for (String k : attributes.keySet()) {
				sb.append(", ").append(k).append("=").append(attributes.get(k));
			}
			sb.append("]");
			return sb.toString();
		}
	}

	class AttributeMatcher {
		private final String name;
		private final String value;

		AttributeMatcher(String name, String value) {
			this.name = name;
			this.value = value;
		}

		boolean matches(ProcessElement pe) {
			return value.equals(pe.getAttribute(name));
		}
	}

	private StringBuilder currentText;
	private ProcessElement processElement;
	private Stack<ProcessElement> processElementStack = new Stack<>();
	private List<ProcessElement> elements = new LinkedList<>();
	private final Map<String, ProcessElement> idToProcessElementMap = new HashMap<>();

	public ActivitiBmpn2XmlReader() {
		factory = XMLInputFactory.newInstance();
		nameToTypeMapping.put("process", Element.PROCESS);
		nameToTypeMapping.put("endEvent", Element.ENDEVENT);
		nameToTypeMapping.put("startEvent", Element.STARTEVENT);
		nameToTypeMapping.put("sequenceFlow", Element.SEQUENCEFLOW);
		nameToTypeMapping.put("parallelGateway", Element.PARALLELGATEWAY);
		nameToTypeMapping.put("serviceTask", Element.SERVICETASK);
	}

	public void readXml(String input) throws XMLStreamException {
		readXml(new StringReader(input));
	}

	public void readXml(Reader input) throws XMLStreamException {
		assert elements.size() == 0 : "Method can only be called once";
		XMLStreamReader xmlReader = factory.createXMLStreamReader(input);

		// Before entering into the main loop, properties of the document can be
		// explored.
		assert (xmlReader.getEventType() == XMLEvent.START_DOCUMENT);

		// Iterate until all elements are processed.
		while (xmlReader.hasNext()) {
			int eventType = xmlReader.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				elementStart(xmlReader.getLocalName());
				int attributes = xmlReader.getAttributeCount();
				for (int i = 0; i < attributes; ++i) {
					elementAttribute(xmlReader.getAttributeLocalName(i),
							xmlReader.getAttributeValue(i));
				}
				break;
			case XMLEvent.END_ELEMENT:
				elementEnd(xmlReader.getLocalName());
				break;
			case XMLEvent.CDATA:
			case XMLEvent.SPACE:
			case XMLEvent.CHARACTERS:
				elementText(xmlReader.getText());
				break;
			}
		}
		ValidateReadXml();
	}

	public ProcessDefinition convertLoadedModel()
			throws ProcessBuilderException, ReflectionUtilException {
		String processName = getElement(Element.PROCESS).getId();
		ProcessBuilder pb = new ProcessBuilder(processName);
		//
		ProcessElement startServiceTask = getStartServiceTask();
		constructStart(pb, startServiceTask);
		return pb.buildProcessDefinition();
	}

	/**
	 * ConstructStart represents the start of a President / ProcessPath
	 * ProcessDefinition
	 * 
	 * @param pb
	 * @param nameServiceTask
	 * @return
	 * @throws ProcessBuilderException
	 * @throws ReflectionUtilException
	 */
	private String constructStart(ProcessBuilder pb,
			ProcessElement startServiceTask) throws ProcessBuilderException,
			ReflectionUtilException {
		System.out.println("* constructStart, id: " + startServiceTask.getId());
		assertTrue(startServiceTask.element == Element.SERVICETASK,
				"Only ServiceTask supported here (right now)");
		NewNode node = addTask(pb, startServiceTask);
		if (!node.isNew) {
			throw new RuntimeException("Start node is not new, id="
					+ node.node.getNrOfNodeRules());
		}
		return follow(pb, node.node, startServiceTask);
	}

	private String followContinue(ProcessBuilder pb, ProcessElement pe)
			throws ProcessBuilderException, ReflectionUtilException {
		System.out.println("* followContinue, id: " + pe.getId());
		assertTrue(pe.element == Element.SERVICETASK,
				"Only ServiceTask supported here (right now)");
		NewNode node = addTask(pb, pe);
		if (!node.isNew) {
			throw new RuntimeException("Continue node is not new, id="
					+ node.node.getNrOfNodeRules());
		}
		return follow(pb, node.node, pe);
	}

	private String follow(ProcessBuilder pb, Node node, ProcessElement pe)
			throws ProcessBuilderException, ReflectionUtilException {
		System.out.println("* follow, id: " + pe.getId());
		// Follow all the paths from this Node
		List<Connection> connections = getConnectedRealProcessElementsFor(pe);
		String idOfLastInChain = null;
		for (Connection connection : connections) {
			ProcessElement connectedPe = connection.connectedRealElement;
			System.out.println("* follow, id: " + pe.getId()
					+ ", connected element, id: " + connectedPe.getId()
					+ " of type/peType: " + pe.element + ", pyType: "
					+ connectedPe.GetPyType());
			if (connections.size() == 1) {
				System.out.println("* * id: " + node.getName()
						+ " when(NEXT) -> " + connectedPe.getId());
				node.when(ProcessTask.Result.Next).next(connectedPe.getId());
			} else {
				final String nameWhen = connection.sequenceFlow.getName();
				final Enum<?> result = ReflectionUtil
						.GetEnumForProcessTaskImpl(pe.getClassName(), nameWhen);
				System.out.println("* * id: " + node.getName() + " when("
						+ nameWhen + ") -> " + connectedPe.getId());
				node.when(result).next(connectedPe.getId());
			}
			//
			if (connectedPe.IsFork()) {
				// Keep track of path that leave the fork,
				List<ProcessPathBuilder> pgPaths = new LinkedList<>();
				int pathSuffix = 1;
				String idJoinProcessElement = null;
				for (Connection pgConnection : getConnectedRealProcessElementsFor(connectedPe)) {
					ProcessElement pgPe = pgConnection.connectedRealElement;
					String processPathName = pe.getId() + "." + pathSuffix++;
					ProcessPathBuilder path1 = pb
							.createProcessPath(processPathName);
					pgPaths.add(path1);
					idJoinProcessElement = constructStart(path1, pgPe);
				}
				ProcessElement afterJoinPe = getConnectedRealProcessElementFor(idJoinProcessElement);
				System.out.println("* * addFork, id: " + pe.getId()
						+ " withJoin: " + idJoinProcessElement + " next: "
						+ afterJoinPe.getId());
				pb.addFork(connectedPe.getId(),
						pgPaths.toArray(new ProcessPathBuilder[pgPaths.size()]))
						.withJoin(idJoinProcessElement)
						.next(afterJoinPe.getId());
				//
				idOfLastInChain = setIdOfLastInChain(pe, idOfLastInChain,
						followContinue(pb, afterJoinPe));
			} else if (connectedPe.IsJoin()) {
				assertTrue(pb.hasParent(),
						"Join found without preceding Fork, joind id: "
								+ connectedPe.getId());
				System.out.println("* * join, end of path");
				idOfLastInChain = setIdOfLastInChain(pe, idOfLastInChain,
						connectedPe.getId());
			} else if (connectedPe.IsServiceTask()) {
				System.out.println("* * addTask, id: " + connectedPe.getId());
				NewNode newNode = addTask(pb, connectedPe);
				if (newNode.isNew) {
					idOfLastInChain = setIdOfLastInChain(pe, idOfLastInChain,
							follow(pb, newNode.node, connectedPe));
				} else {
					System.out.println("* * existing node, id: "
							+ connectedPe.getId());
				}
			} else if (connectedPe.IsEndEvent()) {
				System.out.println("* * add(endTask), id: " + pe.getId());
				pb.addTask(connectedPe.getId(), EndTask.class);
			} else {
				throw new NotImplementedException("Not supported: " + pe);
			}
		}
		return idOfLastInChain;
	}

	String setIdOfLastInChain(final ProcessElement pe, final String currentValue, final String newValue) {
		if (currentValue != null && newValue != null) {
			throw new RuntimeException(
					"setIdOfLastInChain already set, current value="
							+ currentValue + ", newValue=" + newValue);
		}
		System.out.println("* * setIdOfLastInChain, id=" + pe.getId()+", value="+newValue);
		return newValue != null ? newValue : currentValue;
	}

	class NewNode {
		public final boolean isNew;
		public final Node node;

		NewNode(final Node node, final boolean isNew) {
			this.isNew = isNew;
			this.node = node;
		}
	}

	private NewNode addTask(ProcessBuilder pb, ProcessElement pe)
			throws ProcessBuilderException {
		Node node = pb.getNode(pe.getId());
		boolean isNew = node == null;
		if (isNew) {
			final Class<? extends ProcessTask> cls = ReflectionUtil
					.GetProcessTask(pe.getClassName());
			System.out.println("* * addTask, id: " + pe.getId());
			node = pb.addTask2(pe.getId(), cls);
		} else {
			System.out.println("* * addTask, existing node id: " + pe.getId());
		}
		return new NewNode(node, isNew);
	}

	/**
	 * return the start Service task.
	 * 
	 * @param element
	 * @return
	 */
	private ProcessElement getStartServiceTask() {
		String id = getElement(Element.STARTEVENT).getId();
		String nameStart = getElement(Element.SEQUENCEFLOW,
				new AttributeMatcher("sourceRef", id)).getTargetRef();
		return getServiceTaskById(nameStart);
	}

	/**
	 * Return the "real" ProcessElement connected to the provided ProcessElement
	 * 
	 * @param element
	 * @return
	 */
	private ProcessElement getConnectedRealProcessElementFor(
			ProcessElement sourceElement) {
		List<Connection> list = getConnectedRealProcessElementsFor(sourceElement);
		assertTrue(list.size() == 1, "Nr of connected entities to id: "
				+ sourceElement.getId() + " must be 1, is: " + list.size());
		return list.get(0).connectedRealElement;
	}

	private ProcessElement getConnectedRealProcessElementFor(String id) {
		ProcessElement pe = getElementById(id);
		assertIsRealProcessElement(pe);
		return getConnectedRealProcessElementFor(pe);
	}

	/**
	 * Return the "real" ProcessElement's connected to the provided
	 * ProcessElement
	 * 
	 * @param element
	 * @return
	 */
	static class Connection {
		private final ProcessElement sequenceFlow;
		private final ProcessElement connectedRealElement;

		Connection(final ProcessElement sequenceFlow,
				final ProcessElement connectedRealElement) {
			this.sequenceFlow = sequenceFlow;
			this.connectedRealElement = connectedRealElement;
		}
	}

	private List<Connection> getConnectedRealProcessElementsFor(
			ProcessElement sourceElement) {
		List<Connection> list = new LinkedList<>();
		List<ProcessElement> links = getElements(Element.SEQUENCEFLOW,
				new AttributeMatcher("sourceRef", sourceElement.getId()));
		assert links.size() > 0 : "No processElement found connected to id: "
				+ sourceElement.getId();
		for (ProcessElement pe : links) {
			String idConnectedRealProcessElement = pe.getTargetRef();
			ProcessElement connectedRealElement = getElementById(idConnectedRealProcessElement);
			assertIsRealProcessElement(connectedRealElement);
			list.add(new Connection(pe, connectedRealElement));
		}
		return list;
	}

	private ProcessElement getElement(Element element) {
		System.out.println("getElement: " + element);
		for (ProcessElement pe : elements) {
			if (pe.element == element) {
				return pe;
			}
		}
		throw new RuntimeException("Element not found: " + element);
	}

	private ProcessElement getElement(Element element, AttributeMatcher matcher) {
		System.out.println("getElement: " + element + " [attr,value]==["
				+ matcher.name + "," + matcher.value + "]");
		for (ProcessElement pe : elements) {
			if (pe.element == element && matcher.matches(pe)) {
				return pe;
			}
		}
		throw new RuntimeException("Element not found: " + element);
	}

	private List<ProcessElement> getElements(Element element,
			AttributeMatcher matcher) {
		List<ProcessElement> list = new LinkedList<>();
		System.out.println("getElement: " + element + " [attr,value]==["
				+ matcher.name + "," + matcher.value + "]");
		for (ProcessElement pe : elements) {
			if (pe.element == element && matcher.matches(pe)) {
				list.add(pe);
			}
		}
		assertTrue(list.size() > 0, "Elements not found: " + element);
		return list;
	}

	private ProcessElement getElement(AttributeMatcher matcher) {
		System.out.println("getElement: [attr,value]==[" + matcher.name + ","
				+ matcher.value + "]");
		for (ProcessElement pe : elements) {
			if (matcher.matches(pe)) {
				return pe;
			}
		}
		assertTrue(false, "Element not found with [attr,value]==["
				+ matcher.name + "," + matcher.value + "]");
		return null; // please compiler
	}

	private ProcessElement getRealProcessElementById(String id) {
		ProcessElement pe = idToProcessElementMap.get(id);
		assertTrue(pe != null, "ProcessElement not found, id: " + id);
		return pe;
	}

	private ProcessElement getElementById(String id) {
		ProcessElement pe = idToProcessElementMap.get(id);
		assertTrue(pe != null, "ProcessElement not found, id: " + id);
		return pe;
	}

	private ProcessElement getServiceTaskById(String id) {
		ProcessElement pe = idToProcessElementMap.get(id);
		assertTrue(pe != null, "ProcessElement not found, id: " + id);
		assertTrue(pe.element == Element.SERVICETASK,
				"ProcessElement is not a ServiceTask, id: " + id);
		return pe;
	}

	private void assertIsRealProcessElement(ProcessElement pe) {
		Element element = pe.element;
		assertTrue(
				element == Element.SERVICETASK
						|| element == Element.PARALLELGATEWAY
						|| element == Element.ENDEVENT,
				"Error processElement with id: "
						+ pe.getId()
						+ " is of type: "
						+ element
						+ ", supported is servcieTask, ParallelGateway or EndEvent");
	}

	private void elementStart(String localName) {
		currentText = new StringBuilder(256);
		Element currentElement = nameToTypeMapping.get(localName);
		if (currentElement != null) {
			if (processElement != null) {
				System.out.println("elementStart, pushed element: "
						+ processElement.element);
				processElementStack.push(processElement);
			}
			processElement = new ProcessElement(currentElement);
			elements.add(processElement);
		} else {
			processElement = null;
		}
	}

	private void elementEnd(String localName) {
		// verify that processElement has an Id and it is unique.
		if (processElement != null) {
			System.out.println("elementEnd: " + processElement);
			String id = processElement.getId();
			assertTrue(id != null, "Element is missing attribute: id");
			if (processElement.getName() == null) {
				processElement.addAttribute("name", id);
			}
			if (processElement.element == Element.SEQUENCEFLOW) {
				assertTrue(processElement.getSourceRef() != null
						&& processElement.getTargetRef() != null,
						"Element is missing attribute: sourceRef or targetRef");
			}
			if (idToProcessElementMap.containsKey(id)) {
				throw new RuntimeException("Duplicate id");
			}
			idToProcessElementMap.put(id, processElement);
			//
			if (processElementStack.isEmpty()) {
				processElement = null;
			} else {
				processElement = processElementStack.pop();
				System.out.println("elementEnd, popped element: "
						+ processElement.element);
			}
		}
	}

	private void elementText(String text) {
		// not used.
	}

	private void elementAttribute(String localName, String value) {
		if (processElement != null) {
			processElement.addAttribute(localName, value);
		}
	}

	private void ValidateReadXml() {
		for (ProcessElement pe : elements) {
			if (pe.element == Element.PARALLELGATEWAY) {
				List<ProcessElement> connectionsTo = getElements(
						Element.SEQUENCEFLOW, new AttributeMatcher("targetRef",
								pe.getId()));
				List<ProcessElement> connectionsFrom = getElements(
						Element.SEQUENCEFLOW, new AttributeMatcher("sourceRef",
								pe.getId()));
				if (connectionsTo.size() == 1) {
					// FORK
					assertTrue(connectionsFrom.size() > 1, "Invalid gateway: "
							+ pe + "\n  It must be 1-n or n-1");
					pe.SetTypeFork();
					// Connecting a Fork directory to a Join is not allowed.
					for (ProcessElement peFork : connectionsFrom) {
						assertTrue(
								getElementById(peFork.getTargetRef())
										.GetPyType() != PeType.JOIN,
								"Fork can not be connected to Join: "
										+ pe.getId());
					}
				} else if (connectionsFrom.size() == 1) {
					// JOIN
					assertTrue(connectionsTo.size() > 1, "Invalid gateway: "
							+ pe + "\n  It must be 1-n or n-1");
					pe.SetTypeJoin();
				} else {
					assertTrue(false, "Invalid gateway: " + pe
							+ "\n  It must be 1-n or n-1");
				}
			}
			if (pe.element == Element.SERVICETASK) {
				// A service task must have a java Class associated with it.
				String className = pe.getAttribute("class");
				if (className == null) {
					throw new RuntimeException(
							"Service task has no class defined: " + pe);
				}
				if (!ReflectionUtil.DoesClassExist(className)) {
					System.out.println("* service task, id: " + pe.getId()
							+ " class not found: " + className);
				}
			}
			if (pe.element == Element.SEQUENCEFLOW) {
				if (pe.getName() == null) {
					// NEXT is the default name
					pe.addAttribute("name", "NEXT");
				}
			}
		}
	}

	void assertTrue(boolean b, String s) {
		if (!b) {
			throw new RuntimeException(s);
		}
	}

	static public class TempTaskOkNok implements ProcessTask {

		@ProcessTaskResult
		public enum Result {
			OK, NOK
		}

		@Override
		public Enum<?> execute() {
			System.out.println("TempTaskOkNok invoked, returning OK");
			return Result.OK;
		}
	}

}