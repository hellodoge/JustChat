package messenger.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ServerMain {
	private static Server server;

	public static void mainMethod(String[] args) {
		server = new Server(configServerFromArgs(args));
		System.out.println("Starting JustChat Server 1.10 at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		consoleControl();
	}
	private static void consoleControl() {
		Scanner systemStrInput = new Scanner(System.in);
		boolean controlRunning = true;
		while (controlRunning) {
			System.out.print((char)27 + "[32m" + server.getName() + "@" + server.getPort() + (char)27 + "[37m" + ": ");
			String[] currentCommand = systemStrInput.nextLine().split(" ");
			if (currentCommand.length == 0) continue;
			switch (currentCommand[0]) {
				case "set":
					if (currentCommand.length != 3) {
						System.out.println("Usage: set name [server's name]");
						break;
					}
					switch (currentCommand[1]) {
						case "name":
							server.setName(currentCommand[2]);
							server.addMessage(messenger.network.Protocol.setTypePacketName(currentCommand[2]));
							break;
					}
					break;
				case "exit":
					controlRunning = false;
				case "stop":
					System.out.println("Server is shutting down...");
					server.shutDown();
					break;
				case "monitor":
				case "mon":
					System.out.println("Server is " + (server.isServerIsRunning() ? "running" : "stopped"));
					System.out.println("Thread '" + server.serverRun.getName() + "' is " + (server.serverRun.isAlive() ? "running" : "stopped"));
					System.out.println("Thread '" + server.receive.getName() + "' is " + (server.receive.isAlive() ? "running" : "stopped"));
					System.out.println("Thread '" + server.manage.getName() + "' is " + (server.manage.isAlive() ? "running" : "stopped"));
					break;
				case "messages":
				case "mes":
					server.printMessages();
					break;
				case "clients":
				case "cli":
					server.printClients();
					break;
				default:
					System.out.println("Unrecognized option '" + currentCommand[0] +"'");
			}
		}
	}
	private static ServerConfiguration configServerFromArgs(String[] args) {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf("-") == 0) {
				switch (args[i]) {
					case "-p":
					case "--port":
						if (i+1 >= args.length) {
							messenger.Application.configProblems("Port number not found");
						} else if (args[i+1].matches("\\d+")) {
							serverConfiguration.setPort(Integer.parseInt(args[i+1]));
						} else {
							messenger.Application.configProblems("Port number must include only digits");
						}
						break;
					case "-n":
					case "--name":
						if (i+1 >= args.length) {
							messenger.Application.configProblems("Server name not found");
						} else {
							serverConfiguration.setName(args[i+1]);
						}
						break;
					case "--pass":
						if (i+1 >= args.length) {
							messenger.Application.configProblems("Password not found");
						} else {
							serverConfiguration.setPassword(args[i+1]);
						}
						break;
					default:
						messenger.Application.configProblems("Unrecognized option '" + args[i] +"'");
				}
			}
		}
		return serverConfiguration;
	}
}
