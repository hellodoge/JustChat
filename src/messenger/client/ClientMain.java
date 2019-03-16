package messenger.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientMain {
	private static Client client;

	public static void mainMethod(String[] args) {
		client = new Client(configClientFromArgs(args));
		System.out.println("Starting JustChat Client 1.10 at " + (String) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		consoleControl();
	}
	private static ClientConfiguration configClientFromArgs(String[] args) {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf("-") == 0) {
				switch (args[i]) {
					case "--ip":
						if (i + 1 >= args.length) {
							messenger.Application.configProblems("Server ip not found");
							System.exit(1);
						} else {
							clientConfiguration.setIp(args[i + 1]);
						}
						break;
					case "-n":
					case "--name":
						if (i + 1 >= args.length) {
							messenger.Application.configProblems("Name is not found");
							System.exit(1);
						} else {
							clientConfiguration.setName(args[i + 1]);
						}
						break;
					case "-p":
					case "--port":
						if (i + 1 >= args.length) {
							messenger.Application.configProblems("Port is not found");
							System.exit(1);
						} else {
							clientConfiguration.setPort(Integer.parseInt(args[i + 1]));
						}
						break;
					default:
						messenger.Application.configProblems("Unrecognised option '" + args[i] + "'");
						System.exit(1);
				}
			}
		}
		if (clientConfiguration.getIp() == null) {
			messenger.Application.configProblems("Server's ip not found");
			System.exit(1);
		}
		return clientConfiguration;
	}
	private static void consoleControl() {
		Scanner systemStrInput = new Scanner(System.in);
		do {
			client.printConsolePrefix();
			String currentCommand = systemStrInput.nextLine();
			if (currentCommand.isEmpty()) continue;
			if (currentCommand.indexOf("$") != 0) {
				client.addMessage(messenger.network.Protocol.setTypePacketMessage(currentCommand));
			} else {
				switch (currentCommand) {
					case "$exit":
						client.shutDown();
						break;
					default:
						System.out.println("Unrecognized option '" + currentCommand +"'");
				}
			}
		} while (client.isClientIsRunning());
	}
}