package net.twagame.sandbox.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.twagame.sandbox.serial.testclass.Image;
import net.twagame.sandbox.serial.testclass.Media;
import net.twagame.sandbox.serial.testclass.TestClass;
import net.twagame.serial.TWADeserializer;
import net.twagame.serial.TWASerializer;
import net.twagame.serial.io.MemoryInput;
import net.twagame.serial.io.MemoryOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerialTest
{
	private static Logger log = LoggerFactory.getLogger(SerialTest.class);

	private static final int WARMUP_CYCLES = 4;
	private static final int TIMES_TO_RUN_SUBJECT_CODE = 2000;
	private static final int TIMES_TO_RUN_TEST = 500;

	private static TestClass testclass = new TestClass();

	private static int javaSize;

	private static int twaSize;
	private static MemoryOutput memoryOutput = new MemoryOutput();
	private static MemoryInput memoryInput = new MemoryInput(null);
	private static TWASerializer ts = new TWASerializer(memoryOutput);
	private static TWADeserializer tds = new TWADeserializer(memoryInput);

	private static int kryoSize;
	private static Kryo kryoSerial = new Kryo();

	public static double testRunnable(Runnable r)
	{
		long startNanos;
		long elapsed;
		double average = 0;

		for (int i = 0; i < TIMES_TO_RUN_TEST; i++)
		{
			startNanos = System.nanoTime();

			for (int j = 0; j < TIMES_TO_RUN_SUBJECT_CODE; j++)
			{
				r.run();
			}

			elapsed = System.nanoTime() - startNanos;

			average += (double) elapsed / TIMES_TO_RUN_SUBJECT_CODE;
			if (i > 0)
				average /= 2.0;
		}

		return average;
	}

	public static void main(String[] args)
	{
		Runnable java = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oostream = new ObjectOutputStream(baos);

					oostream.writeObject(testclass);
					oostream.close();

					javaSize = baos.size();

					ObjectInputStream oistream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

					testclass = (TestClass) oistream.readObject();
					oistream.close();
				}
				catch (Exception e)
				{
					log.error("Exception: ", e);
					System.exit(1);
				}
			}
		};

		Runnable twa = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					memoryOutput.reset();
					ts.resetHandles();

					ts.writeObject(testclass);

					twaSize = memoryOutput.size();

					memoryInput.reset(memoryOutput.getBytes());

					testclass = (TestClass) tds.readObject();
				}
				catch (Exception e)
				{
					log.error("Exception: ", e);
					System.exit(1);
				}
			}
		};

		Runnable kryo = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Output out = new Output(512);

					kryoSerial.writeObject(out, testclass);

					kryoSize = out.position();

					Input in = new Input(out.toBytes());

					testclass = kryoSerial.readObject(in, TestClass.class);

					in.close();
					out.close();
				}
				catch (Exception e)
				{
					log.error("Exception: ", e);
					System.exit(1);
				}
			}
		};

		log.debug("Warming up...");

		createTestObject();

		for (int i = 0; i < WARMUP_CYCLES; i++)
		{
			testRunnable(twa);
			testRunnable(kryo);
			testRunnable(java);
		}

		log.debug("Running tests...");

		createTestObject();
		double twaTime = testRunnable(twa);

		createTestObject();
		double javaTime = testRunnable(java);

		createTestObject();
		double kryoTime = testRunnable(kryo);

		log.debug(
			"Java (ObjectOutput/InputStream) round trip time : {} nanoseconds, size = {}, throughput  ~= {} packets/s",
			(int) javaTime,
			javaSize,
			(int) (2 / (javaTime / 1000000000)));

		log.debug(
			"Kryo (All default) round trip time              : {} nanoseconds, size = {}, throughput  ~= {} packets/s",
			(int) kryoTime,
			kryoSize,
			(int) (2 / (kryoTime / 1000000000)));

		log.debug(
			"TWA (Using unsafe access) round trip time       : {} nanoseconds, size = {}, throughput  ~= {} packets/s",
			(int) twaTime,
			twaSize,
			(int) (2 / (twaTime / 1000000000)));

	}

	private static void createTestObject()
	{
		testclass = new TestClass();
		testclass.setMedia(new Media());
		testclass.getCollectionOfDoubleArrays().add(new double[] { 2.72, 3.14 });
		testclass.getCollectionOfDoubleArrays().add(new double[] { 3.14, 2.72 });
		testclass.getImages().add(new Image());
		testclass.getImages().addAll(testclass.getImages());
		testclass.setImages2(testclass.getImages());
		testclass.setDoubledouble(new double[][] { { 1, 2 }, { 3, 4 } });
		testclass.setSomeboolean(true);
		testclass.setSomebyte((byte) 42);
		testclass.setSomedouble(3.1415);
		testclass.setSomefloat(3.14F);
		testclass.setSomeint(-100500);
		testclass.setSomelong(-100500100500L);
		testclass.setSomeshort((short) 100);
		testclass.setSomestring("Some string");
	}
}
