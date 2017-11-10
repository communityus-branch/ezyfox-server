package com.tvd12.ezyfoxserver.testing.response;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.constant.EzyCommand;
import com.tvd12.ezyfoxserver.response.EzyAbstractResponse;
import com.tvd12.test.base.BaseTest;

public class EzyAbstractResponseTest extends BaseTest {

    @Test
    public void test() {
        EzyAbstractResponse response = new EzyAbstractResponse() {
        };
        response.setAppId(1);
        response.setCommand(EzyCommand.APP_ACCESS);
        response.setData(123);
        
        assert response.getAppId() == 1;
        assert response.getCommand() == EzyCommand.APP_ACCESS;
        assert response.getData().equals(new Integer(123));
    }
    
}