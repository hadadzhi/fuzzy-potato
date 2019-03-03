package net.twagame.sandbox.threading.pi;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiMain
{
	private static final int THREADS = 8;
	private static final int RUNS = 10;
	private static final long POINTS = 1000000000;

	private static Logger log = LoggerFactory.getLogger(PiMain.class);
	private static ExecutorService pool = Executors.newFixedThreadPool(THREADS);

	public static void main(String[] args)
	{
		long startNanos = System.nanoTime();

		for (int i = 0; i < RUNS; i++)
		{
			pool.submit(new PiCalculator(POINTS));
		}

		pool.shutdown();

		try
		{
			pool.awaitTermination(5, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			throw new AssertionError();
		}

		long elapsedNanos = System.nanoTime() - startNanos;

		log.info(processResults(PiCalculator.getResultSet()));
		log.info("Time: {} nanoseconds", elapsedNanos);
	}

	private static String processResults(Collection<Double> results)
	{
		assert (results.size() > 1);

		double sum = 0;

		for (double result : results)
		{
			sum += result;
		}

		double average = sum / results.size();
		double deviation = 0;

		for (double result : results)
		{
			deviation += Math.pow(result - average, 2);
		}

		deviation = Math.sqrt(deviation / (results.size() * (results.size() - 1)));

		return String.format("Pi = %.9f Deviation = %.9f", average, deviation);
	}
}
