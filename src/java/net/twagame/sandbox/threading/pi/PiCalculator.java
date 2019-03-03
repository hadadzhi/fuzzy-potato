package net.twagame.sandbox.threading.pi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class PiCalculator implements Runnable
{
	private static final Set<Double> resultSet = Collections.synchronizedSet(new HashSet<Double>());

	private long points;
	private Random random;

	public PiCalculator(long points)
	{
		this.points = points;
		this.random = new Random(System.nanoTime());
	}

	@Override
	public void run()
	{
		long inside = 0;
		double x, y;

		for (long i = 0; i < points; i++)
		{
			//Take random point -1 <= x,y < 1
			x = (random.nextDouble() * 2) - 1;
			y = (random.nextDouble() * 2) - 1;

			if (Math.sqrt((x * x) + (y * y)) < 1)
				++inside;
		}

		resultSet.add(4 * ((double) inside / points));
	}

	public static Set<Double> getResultSet()
	{
		return Collections.unmodifiableSet(resultSet);
	}
}
