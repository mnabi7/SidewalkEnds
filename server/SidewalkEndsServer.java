import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.Socket;

import java.util.ArrayList;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;

public class SidewalkEndsServer {
	private static ServerSocket server;
	private static String port;
	private static String ip;
	private static String host;
	private static String help = 
		"quit [q] - stop listening and exit program\n"+
		"port [p] - print port to connect to\n"+
		"ip   [i] - print ip address to connect to\n"+
		"help [h] - display help commands";

	public static void main(String[] args) {
		System.out.println("Starting server...");
		if (listenSocket()) {
			System.out.printf("Server running\n\tIP=%s\n\tPORT=%s\n", ip, port);
			serverListening();
		} else {
			System.exit(-1);
		}
	}

	private static boolean listenSocket() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(".conf")));
			ArrayList<String> lines = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			String newIp = null;
			String newPort = null;
			for (String l : lines) {
				String[] parts = l.split("=");
				if (parts[0].equals("IP_ADDRESS")) newIp = parts[1];
				if (parts[0].equals("PORT")) newPort = parts[1];
			}
			if (newIp != null && newPort != null) {
				InetSocketAddress endp = 
				new InetSocketAddress(InetAddress.getByName(newIp), Integer.parseInt(newPort));
				server = new ServerSocket();
				server.bind(endp);
				port = newPort;
				ip = newIp;
				return true;
			} else { return false; }
		} catch (IOException err) {
			System.out.println("Could not initialize socket :( => "+err.getMessage());
			return false;
		}
	}

	private static void serverListening() {
		Socket newConn;
		try {
			while (true) {
				newConn = server.accept();
				if (newConn != null) {
					Socket client = newConn;
					// ClientWorker newClient = new ClientWorker(newConn, new BufferedReader(new InputStreamReader(newConn.getInputStream())));
					DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
            DataInputStream dIn = new DataInputStream(client.getInputStream());
            boolean keepGoing = true;
            System.out.println("In run()");
            while (keepGoing) {
            	byte msg = dIn.readByte();
            	switch (msg) {
            		case 0: // Post
	            		System.out.println("We got "+dIn.readUTF());
	            		dOut.write(1);
	            		dOut.flush();
	            		keepGoing = false;
	            		break;
            		case 1: // Get
	            		System.out.println("We got "+dIn.readUTF());
	            		File locs = new File("./locations.csv");
	            		BufferedReader newBr = new BufferedReader(new FileReader(locs));
						String line;
						String toRet = "";
						String delim = "";
						while ((line = newBr.readLine()) != null) {
							toRet += delim + line;
							delim = "|";
						}
						System.out.println("Trying to return... "+toRet);
						dOut.write(1);
						dOut.flush();
						dOut.write(toRet.getBytes());
						dOut.flush();
						keepGoing = false;
						break;
					default:
						System.out.println("Got nothing ?");
						keepGoing = false;
            	}
            }
            System.out.println("Finished reading");
            dOut.close();
            dIn.close();
				}
			}
		} catch (IOException err) {
			System.out.println("Error on server listen! => "+err.getMessage());
			System.exit(-1);
		}
	}
}

class ClientWorker implements Runnable {
	private Thread thread;
	private Socket client;
	private BufferedReader br;
	private int num;
	private static int count = 0;

	ClientWorker(Socket client, BufferedReader br) {
		this.client = client;
		this.br = br;
		this.num = count++;
	}

	@Override
	public void run() {
		try {
            DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
            DataInputStream dIn = new DataInputStream(client.getInputStream());
            boolean keepGoing = true;
            System.out.println("In run()");
            while (keepGoing) {
            	byte msg = dIn.readByte();
            	switch (msg) {
            		case 0: // Post
	            		System.out.println("We got "+dIn.readUTF());
	            		dOut.write(1);
	            		dOut.flush();
	            		keepGoing = false;
	            		break;
            		case 1: // Get
	            		System.out.println("We got "+dIn.readUTF());
	            		File locs = new File("./locations.csv");
	            		BufferedReader newBr = new BufferedReader(new FileReader(locs));
						String line;
						String toRet = "";
						String delim = "";
						while ((line = newBr.readLine()) != null) {
							toRet += delim + line;
							delim = "|";
						}
						System.out.println("Trying to return... "+toRet);
						dOut.write(1);
						dOut.flush();
						dOut.write(toRet.getBytes());
						dOut.flush();
						keepGoing = false;
						break;
					default:
						System.out.println("Got nothing ?");
						keepGoing = false;
            	}
            }
            System.out.println("Finished reading");
            dOut.close();
            dIn.close();
			// String msg;
			// while ((msg = br.readLine()) != null) {
			// 	// handleMessage(line);
			// 	File locs = new File("./locations.csv");
			// 	System.out.println("Msg is "+msg);
			// 	if (msg.equals("get")) {
			// 		BufferedReader newBr = new BufferedReader(new FileReader(locs));
			// 		String line;
			// 		String toRet = "";
			// 		String delim = "";
			// 		while ((line = newBr.readLine()) != null) {
			// 			toRet += delim + line;
			// 			delim = "|";
			// 		}
			// 		toRet = toRet.replace("[", "").replace("]", "");
			// 		System.out.println("Trying to return... "+toRet);
			// 		PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			// 		// out.print(toRet);
			// 		// out.flush();
			// 		// out.close();
			// 	} else {
			// 		String[] parts = msg.split(",");
			// 		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(locs, true));
			// 		String toWrite = msg + "\n";
			// 		out.append(toWrite);
			// 		out.close();
			// 	}
			// }
		} catch(IOException err) {
			System.out.printf("Got error for %d => %s\n", num, err.getMessage());
		}
	}

	public void start() {
		System.out.printf("Thread %d started\n", num);
		if (thread == null) {
			thread = new Thread(this);
			System.out.println("Running thread");
			thread.run();
		}
	}

	private void handleMessage(String msg) {
		try {
			File locs = new File("./locations.csv");
			System.out.println("Msg is "+msg);
			if (msg.equals("get")) {
				BufferedReader newBr = new BufferedReader(new FileReader(locs));
				String line;
				String toRet = "";
				String delim = "";
				while ((line = newBr.readLine()) != null) {
					toRet += delim + line;
					delim = "|";
				}
				toRet = toRet.replace("[", "").replace("]", "");
				System.out.println("Trying to return... "+toRet);
				PrintWriter out = new PrintWriter(client.getOutputStream());
				out.print(toRet);
				out.flush();
				out.close();
			} else {
				String[] parts = msg.split(",");
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(locs, true));
				String toWrite = msg + "\n";
				out.append(toWrite);
				out.close();
			}
		} catch(IOException err) {
			System.out.println("Couldn't write to locations.csv, => "+err.getMessage());
		}
	}
}