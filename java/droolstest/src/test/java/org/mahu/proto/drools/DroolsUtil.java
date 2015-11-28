package org.mahu.proto.drools;

import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class DroolsUtil {

	public static Collection<Object> getAllFacts(final StatefulKnowledgeSession ksession) {
		return ksession.getObjects();
	}

	public static KnowledgeBase createKnowledgeBase(final String... rules) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		StringBuilder sb = new StringBuilder();
		for (final String rule : rules) {
			kbuilder.add(ResourceFactory.newClassPathResource(rule), ResourceType.DRL);
			KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				sb.append("[[file=").append(rule).append("]");
				for (KnowledgeBuilderError error : errors) {
					sb.append("[").append(error.toString().replace("\n", "")).append("]");
				}
				sb.append("]");
			}
		}
		final String errorMessage = sb.toString();
		if (!errorMessage.isEmpty()) {
			throw new IllegalArgumentException(errorMessage);
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}
