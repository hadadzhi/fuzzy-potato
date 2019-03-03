package net.twagame.sandbox.threading;

public class SimpleCounter implements Counter
{
	private long count = 0;

	@Override
	public void increment()
	{
		count++;
	}

	@Override
	public long getCount()
	{
		return count;
	}

	@Override
	public void reset()
	{
		count = 0;
	}
}
