package org.mahu.proto.bigobjects;

import java.util.LinkedList;
import java.util.List;

public class AllocateUtil {

	public static void allocateBigJavaObjectsUntilOutOfMemory() {
		try {
			List<BigObject1> l = new LinkedList<BigObject1>();
			long id = 0;
			while (true) {
				id++;
				System.out.println("Adding object: " + id + " size "
						+ (id * BigObject1.TOTAL_SIZE)/(1024*1024) +" mb");
				l.add(new BigObject1());
			}
		} catch (OutOfMemoryError e) {
			System.out.println("Out of memory exception");
		}
	}
	
	public static void allocateNativeObject(int max) {
		try {
			BigObjectNative boNative = new BigObjectNative();
			final int nrOfBytes = 1000 * 1000;
			int cnt = 0;
			for(int i=0; i<max; i++) {
				boNative.call(nrOfBytes);
				cnt += nrOfBytes;
				System.out.println("Allocated native in total: " + cnt/(1024*1024) + " mb");
			}
		} catch (OutOfMemoryError e) {
			System.out.println("Out of memory exception");
		}
	}

}
