package messenger;

public class Application {
	public static void main(String[] args) {
		if (args[0].equals("server")) {
			messenger.server.ServerMain.mainMethod(args);
		} else {
			messenger.client.ClientMain.mainMethod(args);
		}
	}
	public static void configProblems(String details) {
		System.out.println("Usage: java -jar ChatServer.jar -p 8080");
		System.out.println("Details: " + details);
		System.out.println("ARGUMENTS: \n" +
				"-p/--port: set port. [DEFAULT: 8080]");
		System.exit(1);
	}
}
