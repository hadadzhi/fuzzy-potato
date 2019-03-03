package net.twagame.sandbox.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ListenThread extends Thread
{
	private static final int LISTEN_TIMEOUT = 100;
	private ServerSocket socket;

	public ListenThread(ServerSocket listenSocket) throws SocketException
	{
		this.setName("ListenThread");

		socket = listenSocket;
		socket.setSoTimeout(LISTEN_TIMEOUT);
	}

	@Override
	public void run()
	{
		System.out.println(this.getName() + ": Listening on port " + socket.getLocalPort());

		// While listening, create and start new thread for each client
		while (!interrupted())
			try
			{
				ChatServer.addClientThread(socket.accept());
			}
			catch (SocketTimeoutException e)
			{
				continue;
			}
			catch (IOException e)
			{
				System.out.println(this.getName() + ": Could not start client thread: " + e.getMessage());
			}

		// When not listening anymore, terminate all client threads
		ChatServer.terminateAllClientThreads();

		System.out.println(this.getName() + ": Finished");
	}
}
