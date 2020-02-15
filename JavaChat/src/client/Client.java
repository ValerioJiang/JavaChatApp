package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.ClientInfo;

public class Client {
	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private boolean running;
	private String name;

	public Client(String name, String address, int port){
		try {
			this.name = name;
			this.address = InetAddress.getByName(address);
			this.port = port;

			socket = new DatagramSocket();
			running = true;
			listen();
		}catch(Exception e) {
			e.printStackTrace();
		}
		send("\\con:"+name);
	}

	public void send(String message){
		try {
			if(!message.startsWith("\\")) {
				message = name+" : "+message;
			}
			message += "\\e";  //ending identifier
			byte[] data = message.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			socket.send(packet);
			System.out.println("Sent message to : "+address.getHostAddress()+" : "+port);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void listen() {
		Thread listenThread = new Thread("ChatProgram  Listener") {
			public void run() {
				try {
					while(running) {
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);
						socket.receive(packet);

						String message = new String(data);
						message = message.substring(0, message.indexOf("\\e"));

						//MANAGE MESSAGE
						if(!isCommand(message, packet )) {
							//PRINT MESSAGE
							ClientWindow.printToConsole(message);
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}; listenThread.start();
	}
	private static boolean isCommand(String message, DatagramPacket packet) {


		if(message.startsWith("\\con:")) {
			//RUN CONNECTION CODE
			return true;
		}


		return false;
	}

}
