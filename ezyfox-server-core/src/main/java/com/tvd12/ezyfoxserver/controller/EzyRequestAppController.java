package com.tvd12.ezyfoxserver.controller;

import com.tvd12.ezyfoxserver.EzyApplication;
import com.tvd12.ezyfoxserver.app.EzyAppRequestController;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.context.EzyServerContext;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.event.EzySimpleUserRequestAppEvent;
import com.tvd12.ezyfoxserver.event.EzyUserRequestAppEvent;
import com.tvd12.ezyfoxserver.request.EzyRequestAppParams;
import com.tvd12.ezyfoxserver.request.EzyRequestAppRequest;
import com.tvd12.ezyfoxserver.wrapper.EzyUserManager;

public class EzyRequestAppController 
		extends EzyAbstractServerController 
		implements EzyServerController<EzyRequestAppRequest> {

	@Override
	public void handle(EzyServerContext ctx, EzyRequestAppRequest request) {
	    EzyRequestAppParams params = request.getParams();
	    EzyAppContext appCtx = ctx.getAppContext(params.getAppId());
	    EzyApplication app = appCtx.getApp();
	    EzyAppRequestController requestController = app.getRequestController();
	    
	    // user manager for checking, user must be managed
	    EzyUserManager userManger = appCtx.getApp().getUserManager();
	    
	    EzyUser user = request.getUser();
	    
	    // check user joined app or not to prevent spam request
	    if(!userManger.containsUser(user))
	       throw new IllegalStateException("user " + user.getName() + " hasn't joined app");
	    
        EzyUserRequestAppEvent event = newRequestAppEvent(request);

        // redirect handling to app
        requestController.handle(appCtx, event);
	}
	
	protected EzyUserRequestAppEvent newRequestAppEvent(EzyRequestAppRequest request) {
		return new EzySimpleUserRequestAppEvent(
		        request.getUser(),
		        request.getSession(), 
		        request.getParams().getData());
	}
	
}
