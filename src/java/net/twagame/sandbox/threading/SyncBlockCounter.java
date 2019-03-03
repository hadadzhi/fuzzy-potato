package net.twagame.sandbox.threading;

public class SyncBlockCounter implements Counter
{
	private long count;

	@Override
	public void increment()
	{
		synchronized (this)
		{
			count++;
		}
	}

	@Override
	public long getCount()
	{
		synchronized (this)
		{
			return count;
		}
	}

	@Override
	public void reset()
	{
		synchronized (this)
		{
			count = 0;
		}
	}
}
