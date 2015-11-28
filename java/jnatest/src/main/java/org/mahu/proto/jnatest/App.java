package org.mahu.proto.jnatest;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;

public class App {
	public static void main(String[] args) {
		Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32",
				Kernel32.class);

		// Note the pattern where an object is created in Java, provided to the
		// library to populate with data. This is a typical used pattern, because
		// it makes ownership of data clear. The caller is the owner. 
		SYSTEMTIME time = new SYSTEMTIME();
		INSTANCE.GetSystemTime(time);
		System.out.println("Day of the Week " + time.wDayOfWeek);
		System.out.println("Year :  " + time.wYear);

		SYSTEM_INFO systeminfo = new SYSTEM_INFO();
		INSTANCE.GetSystemInfo(systeminfo);
		System.out.println("Processor Type : " + systeminfo.dwProcessorType);
	}
}
