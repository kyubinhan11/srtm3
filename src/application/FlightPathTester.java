package application;

import static org.junit.Assert.*;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;


public class FlightPathTester {
	GeographicPoint[] arrOfGP = new GeographicPoint[16];
	short[] northEast;
	short[] northWest;
	short[] southEast;
	short[] southWest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {		
		northEast = createTileFile("N006E012.HGT");
		northWest = createTileFile("N006W012.HGT");
		southEast = createTileFile("S006E012.HGT");
		southWest = createTileFile("S006W012.HGT");
		
	}
	
	/* test geographicPoint class */
	@Test 
	public void testGeograpicPoint() throws IOException{		
		// -90<= latitude < 90, -180<= longitude < 180
		// check the boundaries of coordinates are set correctly
		try {
			GeographicPoint invalidRange = new GeographicPoint(90, 12);
			fail("Check out the boundary of the range latitude");
		}
		catch (IOException e) {
			
		}
		
		GeographicPoint validRange = new GeographicPoint(-90, 12);
		validRange = new GeographicPoint(-90, 179.99);
		validRange = new GeographicPoint(89.99, 12);
		
		try {
			GeographicPoint invalidRange = new GeographicPoint(22, 180);
			fail("Check out the boundary of the range of longitude");
		}
		catch (IOException e) {
			
		}	
		validRange = new GeographicPoint(-90, -180);
		validRange = new GeographicPoint(22, 179.99);
		
	}
	
	/* test methods in FlightPath class */
	@Test
	public void testReadTileFile(){
		try {
			// there is a file called "N049W180.HGT" but the size is 0
			short[] file = new FlightPath(arrOfGP).readTileFile("N049W180.HGT");
			fail("valid name but invalid file size");
		}
		catch (IOException e) {
			
		}
		
	}
	
	// test getElevationPath()!
	@Test
	public void testGetElevPath() throws IOException{
		// set the coordinate as the one that is in the very north and west in the tile
		arrOfGP[0] = new GeographicPoint(6.9999, 12);  // for NE
		arrOfGP[1] = new GeographicPoint(6.9999, -12); // for NW
		arrOfGP[2] = new GeographicPoint(-5.0001, 12); // for SE
		arrOfGP[3] = new GeographicPoint(-5.0001, -12);// for SW
		
		// set the coordinate as the one that is in the middle in the tile
		arrOfGP[4] = new GeographicPoint(6.5, 12.5);  // for NE
		arrOfGP[5] = new GeographicPoint(6.5, -11.5); // for NW
		arrOfGP[6] = new GeographicPoint(-5.5, 12.5); // for SE
		arrOfGP[7] = new GeographicPoint(-5.5, -11.5);// for SW
		
		// set the coordinate as the one that is in the corner in the tile
		arrOfGP[8] = new GeographicPoint(6, 12);   // for NE
		arrOfGP[9] = new GeographicPoint(6, -12);  // for NW
		arrOfGP[10] = new GeographicPoint(-6, 12); // for SE
		arrOfGP[11] = new GeographicPoint(-6, -12);// for SW
		
		// set the coordinate as the one that is in the very south and east in the tile
		arrOfGP[12] = new GeographicPoint(6, 12.9999);  // for NE
		arrOfGP[13] = new GeographicPoint(6, -11.0001); // for NW
		arrOfGP[14] = new GeographicPoint(-6, 12.9999); // for SE
		arrOfGP[15] = new GeographicPoint(-6, -11.0001);// for SW
		
		
		FlightPath flightPath = new FlightPath(arrOfGP);
		short[] elevationPath = flightPath.getElevationPath();
		
		// the first element is in the very north and west
		assertEquals("check first element NorthEast", northEast[0], elevationPath[0]);	
		assertEquals("check first element NorthWest", northWest[0], elevationPath[1]);
		assertEquals("check first element southEast", southEast[0], elevationPath[2]);		
		assertEquals("check first element southWest", southWest[0], elevationPath[3]);
		
		// get the middle index using getIndex() and compare them
		assertEquals("check middle element NorthEast", northEast[arrOfGP[4].getIndex()]
				, elevationPath[4]);
		assertEquals("check middle element NorthWest", northWest[arrOfGP[5].getIndex()]
				, elevationPath[5]);
		assertEquals("check middle element SouthEast", southEast[arrOfGP[6].getIndex()]
				, elevationPath[6]);
		assertEquals("check middle element SouthWest", southWest[arrOfGP[7].getIndex()]
				, elevationPath[7]);
		
		// check corner coordinates
		assertEquals("check the corner coordinate in NorthEast", northEast[arrOfGP[8].getIndex()]
				, elevationPath[8]);
		assertEquals("check the corner coordinate in NorthWest", northWest[arrOfGP[9].getIndex()]
				, elevationPath[9]);
		assertEquals("check the corner coordinate in SouthEast", southEast[arrOfGP[10].getIndex()]
				, elevationPath[10]);
		assertEquals("check the corner coordinate in SouthWest", southWest[arrOfGP[11].getIndex()]
				, elevationPath[11]);
		
		// the last element is in the very south and east
		int lastIndex = (1201 * 1201) - 1;
		assertEquals("check the last element in NorthEast", northEast[lastIndex]
				, elevationPath[12]);
		assertEquals("check the last element in NorthWest", northWest[lastIndex]
				, elevationPath[13]);
		assertEquals("check the last element in SouthEast", southEast[lastIndex]
				, elevationPath[14]);
		assertEquals("check the last element in SouthWest", southWest[lastIndex]
				, elevationPath[15]);
		
	}
	
	
	// create a fake tile file with a random starting value
	// and return the original array that is created to compare later
	public short[] createTileFile(String aFileName) throws IOException{
	    Random rand = new Random();
	    short max = 32767, min = -32768;
	    int count = rand.nextInt((max - min) + 1) + min;
		
//		int count = -32768;
		short[] shortArray = new short[1201*1201];	
		for(int i=0; i<1201*1201; i++){
			if(count > 32767) count = -32768;
			shortArray[i] = (short)count;
			count++;			
		}
	
		ByteBuffer byteBuffer = ByteBuffer.allocate(shortArray.length * 2);
		ShortBuffer shortOutputBuffer = byteBuffer.asShortBuffer();
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		shortOutputBuffer.put(shortArray);
		FileChannel out = new FileOutputStream(aFileName).getChannel();
		out.write(byteBuffer);
		out.close();		
		
		return shortArray;
	}	
	
}
