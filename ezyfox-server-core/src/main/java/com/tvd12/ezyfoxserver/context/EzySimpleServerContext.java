package com.tvd12.ezyfoxserver.context;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.EzyComponent;
import com.tvd12.ezyfoxserver.EzyServer;
import com.tvd12.ezyfoxserver.command.EzyCloseSession;
import com.tvd12.ezyfoxserver.command.EzyFireEvent;
import com.tvd12.ezyfoxserver.command.EzySendResponse;
import com.tvd12.ezyfoxserver.command.EzyShutdown;
import com.tvd12.ezyfoxserver.command.impl.EzyCloseSessionImpl;
import com.tvd12.ezyfoxserver.command.impl.EzyFireEventImpl;
import com.tvd12.ezyfoxserver.command.impl.EzySendResponseImpl;
import com.tvd12.ezyfoxserver.command.impl.EzyServerShutdownImpl;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.event.EzyEvent;
import com.tvd12.ezyfoxserver.exception.EzyZoneNotFoundException;
import com.tvd12.ezyfoxserver.response.EzyResponse;
import com.tvd12.ezyfoxserver.setting.EzyZoneSetting;

import lombok.Getter;
import lombok.Setter;

public class EzySimpleServerContext extends EzyAbstractComplexContext implements EzyServerContext {

	@Setter
	@Getter
	protected EzyServer server;
	protected EzyFireEvent fireEvent;
	protected EzySendResponse sendResponse;
	
	@Getter
	protected final List<EzyZoneContext> zoneContexts = new ArrayList<>();
	protected final Map<Integer, EzyZoneContext> zoneContextsById = new ConcurrentHashMap<>();
    protected final Map<String, EzyZoneContext> zoneContextsByName = new ConcurrentHashMap<>();
	
	
    @Override
    protected void init0() {
        this.fireEvent = new EzyFireEventImpl(this); 
        this.sendResponse = new EzySendResponseImpl(server);
        this.properties.put(EzyFireEvent.class, fireEvent);
        this.properties.put(EzySendResponse.class, sendResponse);
        this.properties.put(EzyShutdown.class, new EzyServerShutdownImpl(this));
        this.properties.put(EzyCloseSession.class, new EzyCloseSessionImpl(this));
    }
    
	@Override
	public <T> T get(Class<T> clazz) {
	    if(containsKey(clazz))
            return getProperty(clazz);
		throw new IllegalArgumentException("has no instance of " + clazz);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public <T> T cmd(Class<T> clazz) {
	    if(commandSuppliers.containsKey(clazz))
            return (T) commandSuppliers.get(clazz).get();
        throw new IllegalArgumentException("has no command of " + clazz);
	}
	
	@Override
	public void fireEvent(EzyConstant type, EzyEvent event) {
	    fireEvent.fire(type, event);
	}
	
	@Override
	public void send(EzyResponse response, 
	        EzySession recipient, boolean immediate) {
	    sendResponse.execute(response, recipient, immediate);
	}
	
	@Override
	public void send(EzyResponse response, 
	        Collection<EzySession> recipients, boolean immediate) {
	    sendResponse.execute(response, recipients, immediate);
	}
	
	public void addZoneContexts(Collection<EzyZoneContext> zoneContexts) {
        for(EzyZoneContext ctx : zoneContexts)
            addZoneContext(ctx.getZone().getSetting(), ctx);
    }
	
	public void addZoneContext(EzyZoneSetting zone, EzyZoneContext zoneContext) {
	    zoneContexts.add(zoneContext);
	    zoneContextsById.put(zone.getId(), zoneContext);
	    zoneContextsByName.put(zone.getName(), zoneContext);
	    addAppContexts(((EzyAppContextsFetcher)zoneContext).getAppContexts());
	    addPluginContexts(((EzyPluginContextsFetcher)zoneContext).getPluginContexts());
	}
	
	@Override
	public EzyZoneContext getZoneContext(int zoneId) {
	    if(zoneContextsById.containsKey(zoneId))
	        return zoneContextsById.get(zoneId);
	    throw new EzyZoneNotFoundException(zoneId);
	}
	
	@Override
	public EzyZoneContext getZoneContext(String zoneName) {
	    if(zoneContextsByName.containsKey(zoneName))
	        return zoneContextsByName.get(zoneName);
	    throw new EzyZoneNotFoundException(zoneName);
	}
	
	@Override
	protected EzyComponent getComponent() {
	    return (EzyComponent) server;
	}
	
	@Override
	public void destroy() {
	    super.destroy();
	    destroyServer();
	    destroyZoneContexts();
	}
	
	@Override
	protected void clearProperties() {
	    super.clearProperties();
	    this.server = null;
	    this.fireEvent = null;
	    this.sendResponse = null;
	    this.zoneContexts.clear();
	    this.zoneContextsById.clear();
	    this.zoneContextsByName.clear();
	}
	
	private void destroyServer() {
	    processWithLogException(() -> ((EzyDestroyable)server).destroy());
	}
	
	private void destroyZoneContexts() {
        zoneContextsById.values().forEach(this::destroyZoneContext);
    }
	
	private void destroyZoneContext(EzyZoneContext zoneContext) {
	    processWithLogException(() -> ((EzyDestroyable)zoneContext).destroy());
	}
	
}
