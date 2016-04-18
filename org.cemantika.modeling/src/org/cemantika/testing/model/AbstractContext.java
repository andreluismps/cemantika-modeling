/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cemantika.testing.util.GsonUtils;
import org.cemantika.testing.util.RuntimeTypeAdapterFactory;
import org.cemantika.uml.model.HashCodeUtil;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
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
	
	
	private transient Integer id;

	private List<AbstractContext> lstContext = new ArrayList<AbstractContext>();
    
    private String name;
    
	private static final RuntimeTypeAdapterFactory<AbstractContext> adapter = RuntimeTypeAdapterFactory.of(AbstractContext.class);

	private static final HashSet<Class<?>> registeredClasses = new HashSet<Class<?>>();

	static {
		//TODO register for first activation of situation tab
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
    	AbstractContext clone = (AbstractContext) super.clone();
    	clone.setContextList(new ArrayList<AbstractContext>(this.getContextList()));
    	for (AbstractContext abstractContext : clone.getContextList()){
    		abstractContext = abstractContext.clone();
    	}
      return clone;
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

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	protected void addFocusListener(final Text textField, final Field classField, final Object instance){
		textField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				try {
					classField.setAccessible(true);
					if (classField.getType().equals(double.class))
						classField.setDouble(instance, Double.parseDouble(textField.getText()));
					else if (classField.getType().equals(String.class))
						classField.set(instance, textField.getText());
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
	}
	
	protected void addSelectionListener(final Button checkField, final Field classField, final Object instance){
		checkField.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				classField.setAccessible(true);
				
				try {
					classField.setBoolean(instance, checkField.getSelection());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
