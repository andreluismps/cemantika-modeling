package org.cemantika.uml.model;

import org.cemantika.uml.util.UmlUtils;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.UseCase;

public class Focus {

	private static final String BEHAVIOR_TAG = "behavior";
	private Association association;
	private UseCase task;
	private Actor agent;

	public Association getAssociation() {
		return association;
	}

	public void setAssociation(Association association) {
		this.association = association;
	}

	public UseCase getTask() {
		return task;
	}

	public void setTask(UseCase task) {
		this.task = task;
	}

	public Actor getAgent() {
		return agent;
	}

	public void setAgent(Actor agent) {
		this.agent = agent;
	}

	@Override
	public String toString() {
		return String.format("%s executes %s", agent.getName(), task.getName());
	}

	public String getBehaviorVariation() {
		return (String) UmlUtils.getStereotypePropertyValue(association,
				UmlUtils.FOCUS_STEREOTYPE, BEHAVIOR_TAG);
	}

	public void setBehavior(String behavior) {
		UmlUtils.setStereotypePropertyValue(this.association,
				UmlUtils.FOCUS_STEREOTYPE, BEHAVIOR_TAG, behavior);
	}

}
