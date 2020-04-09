package io.sslprox.taskserver.packets.stc;

import io.sslprox.taskserver.Client;
import io.sslprox.taskserver.packets.Packet;

public class STC01RegisterServer extends Packet {

	public STC01RegisterServer() {
		super(1);
	}

	@Override
	public void process(Client client) {
	}

}
