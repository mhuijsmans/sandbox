package org.mahu.proto.pngpixeldatatest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mahu.proto.commons.IOUtils;
import org.mahu.proto.commons.MavenUtils;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReaderInt;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

public class ExplorePngjTest {

	/**
	 * This class is a copy & paste & modify of a pngj class.
	 * The objective was to play with pngj functions/properties.
	 */
	@Test
	public void copyFile() throws IOException {

		// final String imageName = "start.png";
		final String imageName = "weird.png";
		MavenUtils utils = new MavenUtils(this);
		File file1 = new File(utils.getTestClassesDir(), "images/" + imageName);
		File file2 = new File(utils.getTargetDir(), "output-images/copy-"
				+ imageName);
		IOUtils.deleteFile(file2);
		
		assertTrue("file not found: " + file1.getAbsolutePath(), file1.exists());

		PngReaderInt pngr = new PngReaderInt(file1);
		ImageInfo imi2 = new ImageInfo(pngr.imgInfo.cols,  pngr.imgInfo.rows, 16, false);
		
		PngWriter pngw = new PngWriter(file2, imi2);
		System.out.println("imgInfo: "+imi2);
		//
		pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL);

//		ar.com.hjg.pngj.FilterType filter = ar.com.hjg.pngj.FilterType
//				.getByVal(ar.com.hjg.pngj.FilterType.FILTER_PRESERVE.val);

//		pngw.setFilterType(filter);
		ImageLineInt iline2 = new ImageLineInt(imi2);
		int[] line2 = iline2.getScanline();
		for (int r = 0; r < imi2.rows; r++) {
			ImageLineInt iline1 = (ImageLineInt) pngr.readRow(r);
			
			//iline2.setFilterType(iline1.getFilterType());
			int[] line1 = iline1.getScanline();
			line2[0] = line1[0];
			System.arraycopy(line1, 0, line2, 0, line2.length);
			pngw.writeRow(iline2);
		}
		pngr.end();
		pngw.end();
	}

}
