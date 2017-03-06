package application;

import java.io.IOException;


public class MainApp {
	public static void main (String[] args) throws IOException{
		// the tile files were created from FlightPathTest.java
		GeographicPoint[] arrOfGP = new GeographicPoint[4];
		arrOfGP[0] = new GeographicPoint(6, 12);
		arrOfGP[1] = new GeographicPoint(6, 12.001); 
		arrOfGP[2] = new GeographicPoint(-6, -12); 
		arrOfGP[3] = new GeographicPoint(-6, -11.999);
		FlightPath flightPath = new FlightPath(arrOfGP);
		
		// *** answer ***
		short[] theElevationPath = flightPath.getElevationPath();					
		
		System.out.println(flightPath.toString());

	}	
	
}
