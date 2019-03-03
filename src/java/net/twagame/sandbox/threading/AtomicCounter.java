package net.twagame.sandbox.threading;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter implements Counter
{
	private AtomicLong counter = new AtomicLong(0);

	@Override
	public void increment()
	{
		counter.getAndIncrement();
	}

	@Override
	public long getCount()
	{
		return counter.get();
	}

	@Override
	public void reset()
	{
		counter.set(0);
	}
}
