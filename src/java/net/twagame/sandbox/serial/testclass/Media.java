package net.twagame.sandbox.serial.testclass;

import java.io.Serializable;

import net.twagame.serial.annotation.TWASerializable;

@SuppressWarnings({ "unused", "serial" })
@TWASerializable
public class Media implements Serializable
{
	private String uri = "http://javaone.com/keynote.mpg";
	private String title = "Javaone Keynote";
	private int width = 640;
	private int height = 480;
	private String format = "video/mpg4";
	private long duration = 18000000;
	private long size = 58982400;
	private int bitrate = 262144;
	private String[] persons = { "Nikita Gorskikh", "Dmitry Kornev" };
	private String copyright = null;
	private Boolean wbool = true;
}
