package org.mahu.proto.pipes;

import java.util.logging.Logger;

import org.junit.Test;
import org.mahu.proto.pipes.api.DataHandler;
import org.mahu.proto.pipes.impl.AsyncStatusReader;
import org.mahu.proto.pipes.impl.DataReader;
import org.mahu.proto.pipes.impl.DataSequencer;
import org.mahu.proto.pipes.impl.DataSink;
import org.mahu.proto.pipes.impl.Serializer;
import org.mahu.proto.pipes.impl.StatusReader;
import org.mahu.proto.pipes.impl.ValidDataCounter;

/**
 * Unit test for simple App.
 */
public class PipesTest {

	private final static Logger LOGGER = Logger.getLogger(PipesTest.class
			.getName());

	// A synchronous chain
	@Test
	public void testSyncChain() {
		try {
			int max = 2;
			// Build the chain
			DataReader dataReader = new DataReader();
			StatusReader statusReader = new StatusReader();
			dataReader.SetNext(statusReader);
			ValidDataCounter validDataCounter = new ValidDataCounter(max);
			statusReader.SetDataAndStatusHandler(validDataCounter);			
			DataSequencer dataSequencer = new DataSequencer();
			validDataCounter.SetDataAndStatusHandler(dataSequencer);
			DataSink dataSink = new DataSink();
			dataSequencer.SetNext((DataHandler) dataSink);
			// call chain, until status for all Data is known
			while (!validDataCounter.IsMaxReached()) {
				LOGGER.info("### Calling start");
				dataReader.start(validDataCounter.GetDelta());
			}
		} catch (Exception e) {
			LOGGER.info("Caught exception");
		}
	}

	// A chain with a async Status Reader	
	@Test
	public void testChain_AsyncStatusReader() {
		try {
			int max = 2;
			// Build the chain
			DataReader dataReader = new DataReader();
			AsyncStatusReader statusReader = new AsyncStatusReader();
			dataReader.SetNext(statusReader);
			Serializer serializer = new Serializer();
			statusReader.SetDataAndStatusHandler(serializer);
			ValidDataCounter validDataCounter = new ValidDataCounter(max);
			serializer.SetDataAndStatusHandler(validDataCounter);
			DataSequencer dataSequencer = new DataSequencer();
			validDataCounter.SetDataAndStatusHandler(dataSequencer);
			DataSink dataSink = new DataSink();
			dataSequencer.SetNext((DataHandler) dataSink);
			//
			while (!validDataCounter.IsMaxReached()) {
				LOGGER.info("### Calling start");
				dataReader.start(validDataCounter.GetDelta());
				LOGGER.info("Waiting for status know for all data");
				serializer.WaitUntilStatusKnowForAllReadData();
				LOGGER.info("Status know for all data");
			}
		} catch (Exception e) {
			LOGGER.info("Caught exception");
		}
	}
	
	// A chain with a async Data and Status Reader	
	@Test
	public void testChain_AsyncDataReader_AsyncStatusReader() {
		/*
		try {
			int max = 2;
			// Build the chain
			TODO
			//
			while (!validDataCounter.IsMaxReached()) {
				LOGGER.info("### Calling start");
				serializer.DataCounterReset()
				int delta = validDataCounter.GetDelta();
				dataReader.start(delta);
				LOGGER.info("Waiting until all data read");
				serializer.WaitUntilDataRead(delta);				
				LOGGER.info("Waiting for status know for all data");
				serializer.WaitUntilStatusKnowForAllReadData();
				LOGGER.info("Status know for all data");
			}
		} catch (Exception e) {
			LOGGER.info("Caught exception");
		}
		*/
	}	
}
