package net.twagame.sandbox.chatserver;

import java.util.Collection;
import java.util.HashSet;

public class Auth
{
	private static final int MAX_NAME_LENGTH = 10;
	private static final String NAME_PATTERN = "[a-zA-Z0-9]+";
	private static Collection<String> names = new HashSet<String>();

	protected static synchronized boolean auth(String name)
	{
		if ((name.length() > MAX_NAME_LENGTH) || !name.matches(NAME_PATTERN) || names.contains(name))
			return false;

		names.add(name);
		return true;
	}

	protected static synchronized void freeName(String name)
	{
		names.remove(name);
	}

	protected static synchronized String[] getNames()
	{
		return names.toArray(new String[0]);
	}
}
