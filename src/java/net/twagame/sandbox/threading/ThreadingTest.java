package net.twagame.sandbox.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadingTest
{
	private static final Logger log = LoggerFactory.getLogger(ThreadingTest.class);
	private static final int THREADS = 1000;
	private static final int INCREMENTS = 100500;

	private static final Counter c = new AtomicCounter();

	public static void main(String[] args) throws InterruptedException
	{
		long startnsSingle = System.nanoTime();

		log.debug("Running single-threaded...");

		for (long i = 0; i < (THREADS * INCREMENTS); i++)
		{
			c.increment();
		}

		c.reset();

		long endns = System.nanoTime();
		long elapsedSingle = endns - startnsSingle;

		log.debug("Running multi-threaded...");

		ExecutorService pool = Executors.newFixedThreadPool(THREADS);

		long startnsMulti = System.nanoTime();

		for (int i = 0; i < THREADS; i++)
		{
			pool.submit(new Runnable() {
				@Override
				public void run()
				{
					for (int i = 0; i < INCREMENTS; i++)
						c.increment();
				}
			});
		}

		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.MINUTES);

		endns = System.nanoTime();
		long elapsedMulti = endns - startnsMulti;

		log.debug("Count:          {}", c.getCount());
		log.debug("Count intended: {}", (long) INCREMENTS * THREADS);
		log.debug("Elapsed (single-threaded): {} ms, elapsed (multi-threaded): {} ms", elapsedSingle / 1000000, elapsedMulti / 1000000);
		log.debug("Counter: {}", c.getClass().getSimpleName());
	}
}
