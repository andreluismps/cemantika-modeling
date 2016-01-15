/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cemantika.testing.util.GsonUtils;
import org.cemantika.testing.util.RuntimeTypeAdapterFactory;
import org.cemantika.uml.model.HashCodeUtil;
import org.reflections.Reflections;

/**
 *
 * @author MHL
 */
public abstract class AbstractContext implements Serializable, Cloneable, Comparable<AbstractContext>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2703014888130627743L;

	private List<AbstractContext> lstContext = new ArrayList<AbstractContext>();
    
    private String name;
    
	private static final RuntimeTypeAdapterFactory<AbstractContext> adapter = RuntimeTypeAdapterFactory.of(AbstractContext.class);

	private static final HashSet<Class<?>> registeredClasses = new HashSet<Class<?>>();

	static {
		GsonUtils.registerType(adapter);
		Reflections reflections = new Reflections("org.cemantika.testing.contextSource");
		
		Set<Class<? extends AbstractContext>> allClasses = reflections.getSubTypesOf(AbstractContext.class);
		reflections = new Reflections("org.cemantika.testing.contextSource");
		allClasses.addAll(reflections.getSubTypesOf(AbstractContext.class));
		for (Class<? extends AbstractContext> clazz : allClasses) {
			adapter.registerSubtype(clazz);
		}
	}

	private synchronized void registerClass() {
		if (!AbstractContext.registeredClasses.contains(this.getClass())) {
			adapter.registerSubtype(this.getClass());
		}
	}

	public AbstractContext() {
		registerClass();
	}
        
    public void addChildContext(AbstractContext context, int timeSlot){
        addChildContext(context);
    }
    
    public abstract void addChildContext(AbstractContext context);
    
    public List<AbstractContext> getContextList(){
        return lstContext;
    }
    
    public void setContextList(List<AbstractContext> list){
        lstContext = list;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void removeChildContext(AbstractContext context){
        lstContext.remove(context);
    }  
    
    
  @Override
  public AbstractContext clone()
  {
    try
    {
      return (AbstractContext) super.clone();
    }
    catch ( CloneNotSupportedException e ) {
      // Kann eigentlich nicht passieren, da Cloneable
      throw new InternalError();
    }
  }

	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, name);
		result = HashCodeUtil.hash(result, lstContext);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractContext))
            return false;
        if (obj == this)
            return true;

        AbstractContext rhs = (AbstractContext) obj;
        return name.equals(rhs.name) && lstContext.equals(rhs.lstContext);
	}

	@Override
	public String toString() {
		
		return name + "["+ lstContext.toString() +"]";
	}

	@Override
	public int compareTo(AbstractContext abstractContext) {
		return this.name.compareTo(abstractContext.name);
	}
	
}
