package com.tvd12.ezyfoxserver.webapi.controller;

import com.tvd12.ezyfox.monitor.EzyMemoryMonitor;
import com.tvd12.ezyfox.monitor.EzyMonitor;
import com.tvd12.ezyfoxserver.databridge.statistics.EzyMemoryPoint;

public class EzyMemoryController {

	protected EzyMonitor monitor;

	public EzyMemoryPoint getDetails() {
		EzyMemoryPoint point = new EzyMemoryPoint();
		EzyMemoryMonitor memoryMonitor = monitor.getMemoryMonitor();
		point.setMaxMemory(memoryMonitor.getMaxMemory());
		point.setFreeMemory(memoryMonitor.getFreeMemory());
		point.setTotalMemory(memoryMonitor.getMaxMemory());
		return point;
	}
	
}
