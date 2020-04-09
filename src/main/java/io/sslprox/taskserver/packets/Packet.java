package io.sslprox.taskserver.packets;

import io.sslprox.taskserver.Client;
import io.sslprox.taskserver.packets.stc.STC01RegisterServer;

public abstract class Packet {

	public static final Packet[] cts_packets = new Packet[] {
			new STC01RegisterServer()
	};

	/* 
	 * cts = ClientToServer
	 * stc = ServerToClient
	 */
	
	public int id;

	public Packet(int id) {
		this.id = id;
	}
	
	
	
	public abstract void process(Client client);
}
