package org.cemantika.testing.model;

import org.cemantika.uml.model.HashCodeUtil;

public class ContextSourceDefectPattern {
	private String contextSourceName;
	private ContextDefectPattern contextDefectPattern;
	
	public ContextSourceDefectPattern(String contextSourceName, ContextDefectPattern contextDefectPattern) {
		this.contextSourceName = contextSourceName;
		this.contextDefectPattern = contextDefectPattern;
	}

	public void setContextSourceName(String contextSourceName) {
		this.contextSourceName = contextSourceName;
	}

	public String getContextSourceName() {
		return contextSourceName;
	}

	public void setContextDefectPattern(ContextDefectPattern contextDefectPattern) {
		this.contextDefectPattern = contextDefectPattern;
	}

	public ContextDefectPattern getContextDefectPattern() {
		return contextDefectPattern;
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, contextSourceName);
		result = HashCodeUtil.hash(result, contextDefectPattern);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContextSourceDefectPattern))
            return false;
        if (obj == this)
            return true;

        ContextSourceDefectPattern rhs = (ContextSourceDefectPattern) obj;
        return contextSourceName.equals(rhs.contextSourceName) && contextDefectPattern.equals(rhs.contextDefectPattern);
	}

	@Override
	public String toString() {
		return contextSourceName + " - " + contextDefectPattern;
	}
	
}
