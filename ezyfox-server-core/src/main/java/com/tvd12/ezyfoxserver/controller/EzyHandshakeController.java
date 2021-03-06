package com.tvd12.ezyfoxserver.controller;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.tvd12.ezyfox.sercurity.EzyBase64;
import com.tvd12.ezyfox.sercurity.EzyKeysGenerator;
import com.tvd12.ezyfoxserver.constant.EzyCoreConstants;
import com.tvd12.ezyfoxserver.context.EzyServerContext;
import com.tvd12.ezyfoxserver.entity.EzyAbstractSession;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.request.EzyHandShakeRequest;
import com.tvd12.ezyfoxserver.request.EzyHandshakeParams;
import com.tvd12.ezyfoxserver.response.EzyHandShakeParams;
import com.tvd12.ezyfoxserver.response.EzyHandShakeResponse;
import com.tvd12.ezyfoxserver.response.EzyResponse;

public class EzyHandshakeController 
		extends EzyAbstractServerController 
		implements EzyServerController<EzyHandShakeRequest> {

	@Override
	public void handle(EzyServerContext ctx, EzyHandShakeRequest request) {
	    EzySession session = request.getSession();
	    EzyHandshakeParams params = request.getParams();
		updateSession(session, params);
		process(ctx, request);
	}
	
	protected void process(EzyServerContext ctx, EzyHandShakeRequest request) {
	    EzySession newsession = request.getSession();
	    EzyHandshakeParams params = request.getParams();
	    String reconnectToken = params.getToken();
	    ((EzyAbstractSession)newsession).setBeforeToken(reconnectToken);
	    EzyResponse response = newHandShakeResponse(newsession);
	    ctx.send(response, newsession);
	}
	
	protected void updateSession(EzySession session, EzyHandshakeParams params) {
		session.setClientId(params.getClientId());
		session.setClientKey(EzyBase64.decode(params.getClientKey()));
		session.setClientType(params.getClientType());
		session.setClientVersion(params.getClientVersion());
		boolean enableEncryption = params.isEnableEncryption();
		if(enableEncryption) {
        		KeyPair keyPair = newKeyPair();
        		PublicKey publicKey = keyPair.getPublic();
        		PrivateKey privateKey = keyPair.getPrivate();
        		session.setPublicKey(publicKey.getEncoded());
            session.setPrivateKey(privateKey.getEncoded());
		}
	}
	
	protected KeyPair newKeyPair() {
        return EzyKeysGenerator.builder()
                .keysize(EzyCoreConstants.SESSION_KEY_SIZE)
                .algorithm(EzyCoreConstants.DATA_ENCRYPTION_ALGORITHM)
                .build().generate();
    }
	
	protected EzyResponse newHandShakeResponse(EzySession session) {
	    EzyHandShakeParams params = new EzyHandShakeParams();
	    params.setToken(session.getToken());
	    params.setClientKey(session.getClientKey());
	    params.setServerPublicKey(session.getPublicKey());
	    return new EzyHandShakeResponse(params);
	}
	
}
