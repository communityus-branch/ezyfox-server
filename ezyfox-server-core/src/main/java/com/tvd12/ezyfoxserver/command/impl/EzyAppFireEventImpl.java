package com.tvd12.ezyfoxserver.command.impl;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.command.EzyAbstractCommand;
import com.tvd12.ezyfoxserver.command.EzyFireEvent;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.controller.EzyEventController;
import com.tvd12.ezyfoxserver.event.EzyEvent;
import com.tvd12.ezyfoxserver.wrapper.EzyEventControllers;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EzyAppFireEventImpl 
		extends EzyAbstractCommand 
		implements EzyFireEvent {

	private final EzyAppContext context;
	
	public EzyAppFireEventImpl(EzyAppContext context) {
		this.context = context;
	}
	
    @Override
	public void fire(EzyConstant type, EzyEvent event) {
	    EzyEventController ctrl = getController(type);
	    getLogger().debug("app: {} fire event: {}, controller = {}", getAppName(), type, ctrl);
		fire(ctrl, event);
	}
	
	protected void fire(EzyEventController ctrl, EzyEvent event) {
		if(ctrl != null)
			handle(ctrl, event);
	}
	
	protected void handle(EzyEventController ctrl, EzyEvent event) {
	    try {
	        ctrl.handle(context, event);
	    }
	    catch(Exception e) {
	        context.handleException(Thread.currentThread(), e);
	    }
	}
	
	protected EzyEventController getController(EzyConstant type) {
		return getEventControllers().getController(type);
	}
	
	protected EzyEventControllers getEventControllers() {
		return context.getApp().getEventControllers();
	}
	
	protected String getAppName() {
	    return context.getApp().getSetting().getName();
	}
}
