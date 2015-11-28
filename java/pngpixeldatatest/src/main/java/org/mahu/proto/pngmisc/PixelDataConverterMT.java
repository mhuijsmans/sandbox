package org.mahu.proto.pngmisc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PixelDataConverterMT extends  PixelDataConverter {

	public PixelDataConverterMT(final int nrOfRows, final int nrOfColumns,
			final byte[] pixelDataIn) {
		super(nrOfRows, nrOfColumns, pixelDataIn);
	}
	
	public byte[] InterleavedRbg16Bit_to_matlabColumnPlanarRbg16BitPixelData_mt() {
		byte[] matlabColumnPlanarPixelData = new byte[srcPixelData.length];
		RbgIndex idx1 = new Interleaved_to_MatlabColumnPlanarIndex(nrOfRows,
				nrOfColumns);
		RbgIndex idx2 = new Interleaved_to_MatlabColumnPlanarIndex(nrOfRows,
				nrOfColumns);
		RbgIndex idx3 = new Interleaved_to_MatlabColumnPlanarIndex(nrOfRows,
				nrOfColumns);		
		transformPixelData_mt(matlabColumnPlanarPixelData, idx1, idx2, idx3);
		return matlabColumnPlanarPixelData;
	}
	
	public byte[] dummy_mt() {
		byte[] matlabColumnPlanarPixelData = new byte[srcPixelData.length];
		RbgIndex idx1 = new DummyIndex();
		RbgIndex idx2 = new DummyIndex();
		RbgIndex idx3 = new DummyIndex();		
		transformPixelData_mt(matlabColumnPlanarPixelData, idx1, idx2, idx3);
		return matlabColumnPlanarPixelData;
	}	

	private void transformPixelData_mt(final byte[] destPixeldata,
			final RbgIndex idx1, final RbgIndex idx2, final RbgIndex idx3) {
		CountDownLatch cdl = new CountDownLatch(3);
		(new Thread(new TransformPixelDataTask(this.srcPixelData,
				destPixeldata, nrOfRows, nrOfColumns, idx1,
				TransformPixelDataTask.Channel.ChannelR, cdl))).start();
		(new Thread(new TransformPixelDataTask(this.srcPixelData,
				destPixeldata, nrOfRows, nrOfColumns, idx2,
				TransformPixelDataTask.Channel.ChannelG, cdl))).start();
		(new Thread(new TransformPixelDataTask(this.srcPixelData,
				destPixeldata, nrOfRows, nrOfColumns, idx3,
				TransformPixelDataTask.Channel.ChannelB, cdl))).start();
		try {
			cdl.await(2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new PixelDataConverterException("Execution interrupted", e);
		}
	}

	static class TransformPixelDataTask implements Runnable {

		enum Channel {
			ChannelR, ChannelG, ChannelB, Dummy
		};

		private final CountDownLatch cdl;
		private final byte[] srcPixeldata;
		private final byte[] destPixeldata;
		private final int nrOfColumns;
		private final int nrOfRows;
		private final RbgIndex idx;
		private final Channel channel;

		TransformPixelDataTask(final byte[] srcPixeldata,
				final byte[] destPixeldata, final int nrOfRows,
				final int nrOfColumns, final RbgIndex idx,
				final Channel channel, final CountDownLatch cdl) {
			this.cdl = cdl;
			this.srcPixeldata = srcPixeldata;
			this.destPixeldata = destPixeldata;
			this.idx = idx;
			this.nrOfColumns = nrOfColumns;
			this.nrOfRows = nrOfRows;
			this.channel = channel;
		}

		@Override
		public void run() {
			try {
				int i = 0;
				byte[] tmpSrc = this.srcPixeldata;
				byte[] tmpDst = this.destPixeldata;
				for (int columnIdx = 0; columnIdx < nrOfColumns; columnIdx++) {
					for (int rowIdx = 0; rowIdx < nrOfRows; rowIdx++) {
						switch (channel) {
						case ChannelR:
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							i += 4;
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							break;
						case ChannelG:
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							i += 2;
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							i += 2;
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							break;
						case ChannelB:
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							idx.nextIndexInSourcePixeldata();
							i += 4;
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							tmpDst[i++] = tmpSrc[idx
									.nextIndexInSourcePixeldata()];
							break;
						case Dummy:
							break;
						}
					}
				}
			} finally {
				cdl.countDown();
			}

		}
	}
	
	public static class DummyIndex implements RbgIndex {

		@Override
		public int nextIndexInSourcePixeldata() {
			return 0;
		}
		
	}

}
