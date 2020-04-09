package io.sslprox.taskserver.packets.cts;

import io.sslprox.taskserver.Client;
import io.sslprox.taskserver.packets.Packet;

public class CTS01RegisterServer extends Packet {

	public String session;
	
	public CTS01RegisterServer() {
		super(1);
	}

	@Override
	public void process(Client client) {
	}


}
