package org.mahu.proto.pngpixeldatatest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.commons.IOUtils;
import org.mahu.proto.commons.MavenUtils;
import org.mahu.proto.commons.TestUtils;
import org.mahu.proto.pngmisc.PngReader;
import org.mahu.proto.pngmisc.PngWriter;

public class PngCreatePerformanceTest {

	// functional test
	@Test
	public void testUseFileOrByteArrayGivesSameResult() throws IOException {
		final String imageName = "start.png";
		// final String imageName = "weird.png";
		MavenUtils utils = new MavenUtils(this);
		File png = new File(utils.getTestClassesDir(), "images/" + imageName);
		assertTrue("file not found: " + png.getAbsolutePath(), png.exists());
		
		byte[] pngRawBytes = IOUtils.readAllBytes(png);
		//
		PngReader rdrBytes = new PngReader(pngRawBytes);
		byte[] pixelDataBytes = rdrBytes.getPixelDataAsBytes();
		//
		PngReader rdrFile = new PngReader(png);
		byte[] pixelDataFile = rdrFile.getPixelDataAsBytes();
		//
		TestUtils.assertBytesEquals(pixelDataBytes, pixelDataFile, 0);
	}

	// Functional test
	@Test
	public void copyImages() throws IOException {
		(new PngCopyTask("start.png", true, true)).createPngFromPixelData();
		(new PngCopyTask("weird.png", true, true)).createPngFromPixelData();
		(new PngCopyTask("big-weird.png", true, true)).createPngFromPixelData();
	}

	@Test
	public void copyXImages() throws IOException {
		PngCopyTask task = new PngCopyTask("weird.png", false, false);
		final int MAX = 10;
		task.doXruns("Xrun", MAX);
	}

	@Test
	public void copyXXImagesUncompressed() throws IOException {
		PngCopyTask task = new PngCopyTask("big-weird.png", false, false);
		final int MAX = 10;
		task.doXruns("XXrun", MAX);
		task.doXruns("XXrun", 10);		
	}
	
	@Test
	public void copyXXImagesCompressionLevel9() throws IOException {
		PngCopyTask task = new PngCopyTask("big-weird-compressionLevel9.png", false, false);
		final int MAX = 10;
		task.doXruns("XXrun", MAX);
	}

	class PngCopyTask {
		private final String imageName;
		private final MavenUtils utils;
		private final File pngIn;
		private final PngReader pngInRdr;
		private final byte[] pixelDataIn;
		private final boolean doAssert;
		private final boolean toDisk;
		private int pngRawBytesOutLength=-1; 

		private PngCopyTask(final String imageName, boolean doAssert,
				boolean toDisk) throws IOException {
			utils = new MavenUtils(this);
			pngIn = new File(utils.getTestClassesDir(), "images/" + imageName);
			assertTrue("file not found: " + pngIn.getAbsolutePath(), pngIn.exists());
			byte[] pngRawBytesIn = IOUtils.readAllBytes(pngIn);
			pngInRdr = new PngReader(pngRawBytesIn);
			pixelDataIn = pngInRdr.getPixelDataAsBytes();
			this.doAssert = doAssert;
			this.imageName = imageName;
			this.toDisk = toDisk;
		}
		
		void doXruns(final String text, final int MAX) throws IOException {
			for (int compressionLevel = 0; compressionLevel < 3;) {
				Chrono chrono = new Chrono();
				for (int i = 0; i < MAX; i++) {
					createPngFromPixelData(compressionLevel);
				}
				System.out.println(text+"; image="+imageName+"; compressionLevel=" + compressionLevel
						+"; PngOutSize="+pngRawBytesOutLength + "; "+chrono.elapsedAndAvg(MAX));
				compressionLevel = (compressionLevel<3) ? compressionLevel+1 : compressionLevel+3; 
			}
		}

		void createPngFromPixelData() throws IOException {
			createPngFromPixelData(0);
		}

		void createPngFromPixelData(final int compressionLevel)
				throws IOException {
			int bitDepth = pngInRdr.getBitDepth();
			//
			int nrOfRows = pngInRdr.getNrOfRows();
			int nrOfColums = pngInRdr.getNrOfColumns();
			PngWriter pngWriter = new PngWriter(compressionLevel);
			byte[] pngRawBytesOut = pngWriter.convertPixelDataToPng(
					pixelDataIn, nrOfRows, nrOfColums, bitDepth);
			pngRawBytesOutLength = pngRawBytesOut.length;
			if (toDisk) {
				File pngOut = new File(utils.getTargetDir(), "output-images/"
						+ imageName);
				IOUtils.deleteFile(pngOut);
				IOUtils.writeBytes(pngOut, pngRawBytesOut);
				//
				if (doAssert) {
					PngReader pngOutRdr = new PngReader(pngOut);
					TestUtils.assertBytesEquals(pixelDataIn,
							pngOutRdr.getPixelDataAsBytes(), 512);
				}
			}
		}
	}

}