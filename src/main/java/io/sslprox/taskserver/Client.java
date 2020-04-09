package io.sslprox.taskserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.sslprox.taskserver.packets.Packet;

public class Client {

	private static final Logger logger = LoggerFactory.getLogger(Client.class);

	private WsContext ctx;

	public Client(WsContext ctx) {
		this.ctx = ctx;
	}

	public WsContext getContext() {
		return ctx;
	}

	public void onMessage(WsMessageContext ctx) {
		Packet packet = ctx.message(Packet.class);
		boolean processed = false;
		for (Packet p : Packet.cts_packets)
			if (p.id == packet.id) {
				p.process(this);
				processed = true;
			}
		if (!processed)
			logger.error("cannot find packet with id " + packet.id);
	}

}
