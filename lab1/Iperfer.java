import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Iperfer{
	final static String prompt = "java Iperfer -c/-s -h <server hostname> -p <server port> -t <time>";

	public static void main(String[] args) throws IOException {
		// handle arguments
	if ((args.length == 0) || args[0].equals("-c") && args.length != 7) {
			System.err.println("Error: missing or additional arguments");
			System.out.printf("%s", prompt); // TODO DEL LATER
			System.exit(1);
		}

		if (args[0].equals("-s") && args.length != 3) {
			System.err.println("Error: missing or additional arguments");
			System.out.printf("%s", prompt); // TODO DEL LATER
			System.exit(1);
		}

		// Client Mode
		if (args[0].equals("-c")) {
			runClient(args);
		}
		// Server Mode
		else if (args[0].equals("-s")) {
			runServer(args);
		}
	}

	// helper method for doing Client Operation
	private static void runClient(String[] args) throws IOException {
		// check port
		int port = Integer.parseInt(args[4]);
		if (port < 1024 || port > 65536) {
			System.err.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}
		// extract arguments
		String host = args[2];
		int runTime = Integer.parseInt(args[6]);

		// create socket
		try (
			Socket socket = new Socket(host, port);
				OutputStream out = socket.getOutputStream();) {
			// try success
			byte[] data = new byte[1000]; // data with 1000 0's
			long byteSent = 0;
			long startTime = System.currentTimeMillis(); // don't know if there is a function for second
			long endTime = startTime + (1000L * runTime);

		while (System.currentTimeMillis() < endTime) {
			byteSent += 1000;
			out.write(data);
		}

		// print bytes sent & rate
		double totalKBSent = byteSent / 1000; // convert from B to KB
		double totalMbSent = (totalKBSent / 1000) * 8; // convert to Mb just for calculating rate
		double rate = totalMbSent / ((endTime - startTime) / 1000); // Mb/s
		System.out.println("total bytes sent: " + totalKBSent + "KB");
		System.out.printf("rate: %.3f Mb/s\n", rate);
		out.flush();
		socket.close();
	} catch (UnknownHostException e) {
			System.err.println("Unknown Host: " + host);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O from: " + host);
			System.exit(1);
		}
	}

	// helper method for doing Server Operation
	private static void runServer(String[] args) throws IOException {
		// check port
		int port = Integer.parseInt(args[2]);
		if (port < 1024 || port > 65536) {
			System.err.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}

	// create socket
	try (
			ServerSocket serverSocket = new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();
			InputStream in = clientSocket.getInputStream();) {
		// try success
		byte[] input = new byte[1000];
		long byteReceive = 0;
		long startTime = System.currentTimeMillis(); // don't know if there is a function for second

		int bytesread;
		while ((bytesread = in.read(input)) != -1) {
			byteReceive += bytesread;
		}

					// print bytes sent & rate
					double totalKiloByteReceive = byteReceive / 1000; // convert from B to KB
					double totalMbReceive = (totalKiloByteReceive / 1000) * 8; // conver to Mb just to calculate rate
					double rate = totalMbReceive / ((System.currentTimeMillis() - startTime) / 1000); // Mb/s
					System.out.println("total Kilobytes recieve: " + totalKiloByteReceive + "KB");
					System.out.printf("rate: %.3f Mb/s\n", rate);
				} catch (IOException e) {
					System.err.println("Exception caught when listening the port");
					System.exit(1);
				}
			}
		}
