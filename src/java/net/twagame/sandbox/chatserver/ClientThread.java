package net.twagame.sandbox.chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread extends Thread
{
	private static final int TIMEOUT = 500;
	private Socket socket;
	private BufferedReader input;
	private PrintStream output;
	private String userName = null;
	private boolean authorized = false;

	public ClientThread(Socket s) throws IOException
	{
		this.setName("ClientThread");

		socket = s;
		s.setSoTimeout(TIMEOUT);

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintStream(socket.getOutputStream(), true);
	}

	@Override
	public void run()
	{
		System.out.println(this.getName() + ": Started");

		// Send the system charset name to the client
		sendMessage(System.getProperty("file.encoding"));

		// Main logic
		String message;
		while (!interrupted())
		{
			try
			{
				message = input.readLine();
			}
			catch (SocketTimeoutException e)
			{
				continue;
			}
			catch (IOException e)
			{
				System.out.println(this.getName() + ": " + e.getMessage() + " --- " + e.toString());
				e.printStackTrace();
				break;
			}

			// If end of input reached
			if (message == null)
				break;

			if (!authorized)
			{
				userName = message;
				System.out.println(this.getName() + ": client sent name: " + message);

				if (!(authorized = Auth.auth(userName)))
				{
					sendMessage("/badname");
					continue;
				}

				System.out.println(this.getName() + ": New user: " + userName);
				this.setName(this.getName() + " (" + userName + ")");

				sendMessage("/name" + userName);
				ChatServer.sendToAll("/newuser" + userName);

				continue;
			}

			// Process commands
			if (message.equals("/exit"))
				break;
			if (message.equals("/users"))
			{
				sendMessage("Connected users:");
				for (String name : Auth.getNames())
					sendMessage("\t" + name);

				continue;
			}

			// Process normal message
			ChatServer.sendToAll(userName + ": " + message);
		}

		// Clean up
		Auth.freeName(userName);
		ChatServer.removeClientThread(this);

		sendMessage("/bye");

		if (authorized)
			ChatServer.sendToAll("/userleft" + userName);

		try
		{
			input.close();
			output.close();
			socket.close();
		}
		catch (IOException e)
		{
			System.out.println(this.getName() + ": " + e.getMessage());
		}

		System.out.println(this.getName() + ": Finished");
	}

	protected void sendMessage(String message)
	{
		output.println(message);
	}

	protected boolean isAuthorized()
	{
		return authorized;
	}
}
