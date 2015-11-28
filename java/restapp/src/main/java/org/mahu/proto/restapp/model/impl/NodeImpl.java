package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgument;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentLengtGtZero;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentNotNull;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.NodeRule;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

class NodeImpl implements Node {
	
	private final ProcessDefinition processDefinition;
	private final String name;
	private final Class<? extends ProcessTask> cls;
	private final List<NodeRuleImpl> rules = new LinkedList<NodeRuleImpl>();
	private boolean isFinal = false;
	private final Enum<?> []results;

	NodeImpl(final ProcessDefinition aProcessDefinition, final String aName, final Class<? extends ProcessTask> aCls) throws ProcessBuilderException {
		checkArgumentNotNull(aProcessDefinition);
		checkArgumentLengtGtZero(aName);
		checkArgumentNotNull(aCls);
		checkClassIsPublic(aCls);
		checkClassHasDefaultCtor(aCls);
		processDefinition = aProcessDefinition;
		name = aName;
		cls = aCls;
		//
		Enum<?> []values = new Enum<?> [0];
		for(Class<?> cl : cls.getDeclaredClasses()) {		
			Annotation ann = cl.getAnnotation(ProcessTaskResult.class);
			if (ann!=null) {
				// http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9.2
				// defines: public static E[] values();
				Method method;
				try {
					method = cl.getMethod("values", new Class[0]);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new ProcessBuilderException("Failed to get static method values()",e);
				}
				try {
					values = (Enum<?>[])method.invoke(null, new Object[0]);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new ProcessBuilderException("Failed to invoke static method values()",e);
				}
				break;
			}
		}	
		results = values;
	}

	public NodeRule when(final Enum<?> aValue) throws ProcessBuilderException {
		checkArgumentNotNull(aValue);
		checkArgument(aValue !=ProcessTask.Result.Null,"ProcessTask.Result.Null can not be used in when");
		checkArgument(!(aValue ==ProcessTask.Result.Next && hasDefinedResults()),"ProcessTask.Result.Next can only be used for Task without own defined results");
		checkState(!isFinal);
		NodeRuleImpl rule = new NodeRuleImpl(this, aValue);
		add(rule);
		return rule;
	}

	public void isFinal() throws ProcessBuilderException {
		// IsFinal can be set for Task with annotated results
		checkState(rules.isEmpty());
		checkState(!isFinal);
		isFinal = true;
	}

	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}	

	@Override
	public Class<? extends ProcessTask> getProcessTaskClass() {
		return cls;
	}
	
	@Override
	public int getNrOfResults() {
		return results.length;
	}
	
	@Override
	public int getNrOfNodeRules() {
		return rules.size();
	}
	
	class NodeRuleEnumerator implements Enumeration<NodeRule> {
		
		private Iterator<NodeRuleImpl> it = rules.iterator();

		@Override
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		@Override
		public NodeRule nextElement() {
			return it.next();
		}
		
	}
	
	@Override
	public Enumeration<NodeRule> nodeRuleEnumeration() {
		return new NodeRuleEnumerator();
	}
	
	public Enum<?> [] getResult() {
		return results;
	}
	
	@Override
	public NodeRuleImpl getNodeRule(final int idx) {
		return rules.get(idx);
	}
	
	public List<NodeRuleImpl> getRules() {
		return rules;
	}
	
	@Override	
	public NodeRuleImpl findRule(Enum<?>e) {
		for(NodeRuleImpl r: getRules()) {
			// Enum's can be compared using ==
			if (e == r.getValue()) {
				return r;
			}
		}
		return null;
	}
	
	private boolean hasDefinedResults() {
		return results.length>0;
	}

	private void add(final NodeRuleImpl rule) {
		// check that result is not used yet
		for (NodeRuleImpl r : rules) {
			// Comparing enums:
			// http://stackoverflow.com/questions/1750435/comparing-java-enum-members-or-equals
			if (r.getValue() == rule.getValue()) {
				throw new IllegalStateException("Enum result is already used: "
						+ r.getValue());
			}
		}
		rules.add(rule);
	}
	
	private void checkClassHasDefaultCtor(Class<? extends ProcessTask> cls)
			throws ProcessBuilderException {
		Constructor<?>[] allConstructors = cls.getDeclaredConstructors();
		for(Constructor<?> ctor: allConstructors) {
			Class<?>[] pType  = ctor.getParameterTypes();
			if (pType.length==0) {
				return;
			}
		}
		throw new ProcessBuilderException("Default constuctor mandatory, but missing for class: "+cls.getName());
	}
	
	private void checkClassIsPublic(Class<? extends ProcessTask> cls) throws ProcessBuilderException {
		if (!Modifier.isPublic(cls.getModifiers())) {
			throw new ProcessBuilderException("Class must be public (to create instance): "+cls.getName());			
		}
	}
}