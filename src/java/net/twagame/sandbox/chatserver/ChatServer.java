package net.twagame.sandbox.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

public class ChatServer
{
	private static final int PORT = 1580;
	private static final int MAXPORT = 65535;
	private static ServerSocket socket;
	private static ListenThread listenThread;
	private static Collection<ClientThread> clientThreads = new HashSet<ClientThread>();
	private static boolean isShuttingDown = false;

	protected static synchronized void addClientThread(Socket socket) throws IOException
	{
		ClientThread thread = new ClientThread(socket);
		thread.start();
		clientThreads.add(thread);
	}

	protected static synchronized void removeClientThread(ClientThread t)
	{
		if (!isShuttingDown)
			clientThreads.remove(t);
	}

	protected static void terminateAllClientThreads()
	{
		for (ClientThread thread : clientThreads)
		{
			thread.interrupt();

			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				// Ignore
			}
		}
		clientThreads.clear();
	}

	protected static synchronized void sendToAll(String message)
	{
		for (ClientThread thread : clientThreads)
			if (thread.isAuthorized())
				thread.sendMessage(message);
	}

	public static void main(String[] args)
	{
		System.out.println("ChatServer: Encoding: " + System.getProperty("file.encoding"));

		int port = PORT;
		while (true)
			try
			{
				socket = new ServerSocket(port);
				break;
			}
			catch (Exception e)
			{
				if (port < MAXPORT)
					++port;
				else
				{
					System.out.println("ChatServer: Could not create server socket: " + e.getMessage());
					System.exit(1);
				}
			}

		// Start listening
		try
		{
			listenThread = new ListenThread(socket);
			listenThread.start();
		}
		catch (SocketException e)
		{
			System.out.println("ChatServer: Could not start listening thread: " + e.getMessage());
			System.exit(1);
		}

		// Set clean up shootdown hook
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				isShuttingDown = true;
				System.out.println("ChatServer: Stopping");
				try
				{
					listenThread.interrupt();
					listenThread.join();

					socket.close();

					System.out.println("ChatServer: Stopped");
				}
				catch (InterruptedException e)
				{
					System.out.println("ChatServer: Shutdown interrupted: " + e.getMessage());
					System.exit(1);
				}
				catch (IOException e)
				{
					System.out.println("ChatServer: Error closing socket: " + e.getMessage());
					System.exit(1);
				}
			}
		});
	}
}
