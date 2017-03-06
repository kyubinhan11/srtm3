package application;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FlightPath {
	private GeographicPoint[] flightPath;	
	private short[] elevationPath;
	private int numOfElevPoint = 1201;
	
	/*constructor*/
	public FlightPath(GeographicPoint[] path){
		this.flightPath = path;
	}
	
	public GeographicPoint[] getPath(){
		return this.flightPath;
	}
	
	public String toString(){
		String str = "";
		for (int i=0; i < this.flightPath.length; i++){
			str = str + "(" + this.flightPath[i].toString()+", elevation: "
					+ elevationPath[i] + "m)\n";
		}
		
		return str;
	}
	
	// return the elevation path 
	public short[] getElevationPath() throws IOException{		
		if(this.flightPath.length == 0) throw new IOException("Invalid flight Path!");		
		
		// store each elevation value in 2 bytes
		elevationPath = new short[this.flightPath.length];		
		
		// initialize a start point
		GeographicPoint startPoint = flightPath[0];
		short[] tile = readTileFile(startPoint.genFileName());		
		int tileIndex = startPoint.getIndex();
		elevationPath[0] = tile[tileIndex];
				
		// iterate the rest of points
		GeographicPoint currPoint, prevPoint;
		for(int i=1; i<flightPath.length; i++){
			currPoint = flightPath[i];  
			prevPoint = flightPath[i-1];
			// only read a tile file when it is needed
			if(!currPoint.isSameDegree(prevPoint)) tile = readTileFile(currPoint.genFileName());				
			tileIndex = currPoint.getIndex();
			elevationPath[i] = tile[tileIndex];			
		}
		
		return elevationPath;
	}
	
	// read a tile file and turn it into a Short array 
	public short[] readTileFile(String aFileName) throws IOException{	    
	    FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(aFileName), StandardOpenOption.READ);
		// make sure the size of the file is 2,884,802
	    if(fc.size() != numOfElevPoint * numOfElevPoint * 2) 
			throw new IOException("invalid tile file!");
	    
	    ByteBuffer byteBuffer = ByteBuffer.allocate((int)fc.size());
		// the byte order is big endian which means the most significant byte comes first
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
	    fc.read(byteBuffer);	    
		byteBuffer.flip();
		
		//extract all the short values that are in the tile file into a Short array
		Buffer buffer = byteBuffer.asShortBuffer();
		short[] shortArray = new short[(int)fc.size()/2];
		((ShortBuffer)buffer).get(shortArray);	
		
		return shortArray;
	    
	}
}
