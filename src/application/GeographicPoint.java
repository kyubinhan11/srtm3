package application;

import java.io.IOException;
import java.text.DecimalFormat;

public class GeographicPoint {
	private double latitude;
	private double longitude;
	private int numOfElevPoint = 1201; 
	
	/* constructor */
	public GeographicPoint(){
		this.latitude = 0; this.longitude = 0;
	}
	
	public GeographicPoint(double lat, double lon) throws IOException{
		// -90<= latitude < 90, -180<= longitude < 180
		if(lat >= 90 || lat < -90) throw new IOException("Invalid latitude!");
		if(lon >= 180 || lon < -180) throw new IOException("Invalid longitude!");
		this.latitude = lat;
		this.longitude = lon;
		
	}
	/* end of constructor*/
	
	// generate a filename for reading the tile file ex. N006E160.HGT.
	public String genFileName(){		
		double lat = this.latitude;
		double lon = this.longitude;
		int intLat = (int) lat;
		int intLon = (int) lon;
		DecimalFormat decimalFormat = new DecimalFormat("000");
		
		String fn = "";
		
		if(lat >= 0) {
			fn += "N";		
			fn += decimalFormat.format(intLat);	
		}
		else {
			fn += "S";
			// when the degree is just an integer -31 I should look at -31(S31)
			// when it contains decimal like -30.xx I should also look at -31(S31)
			// it is because -30.xx is in the tile: (-31, -30.9, ... 30.5 ..., 30.00001)
			// The method floor gives the largest integer that is less than or equal to the argument.
			fn += decimalFormat.format(Math.abs(Math.floor(lat)));
		}		
			
		if(lon >= 0) {
			fn += "E";
			fn += decimalFormat.format(intLon);
		}
		else {
			fn += "W";
			// when the degree is just an integer -143 I should look at -143(W143)
			// when it contains decimal like -142.xx I should also look at -143(W143)
			// it is because -142.xx is in the tile: (-143, -142.9, ... 142.5 ..., 142.00001)
			// and the next tile: (-142, -141.9...			
			fn += decimalFormat.format(Math.abs(Math.floor(lon)));
			
		}		
		fn += ".HGT";
		
		System.out.println("the current tile file is " + fn);
		
		return fn; 
	}
	
	/*
	 * It would be a waste to read a tile file every time so
	 * check to see if the latitude and longitude of the two points 
	 * share the same integer and have decimal values and when they do I can use the same tile file
	 */
	public boolean isSameDegree(GeographicPoint other){
		double deciLat = this.getLat() % 1, deciLon = this.getLong() % 1, 
				deciOthLat = other.getLat() % 1, deciOthLon = other.getLong() % 1;
		
		if((int)this.getLat() == (int)other.getLat() && (int)this.getLong() == (int)other.getLong()
				&& deciLat !=0 && deciLon !=0 && deciOthLat !=0 && deciOthLon != 0) 
			return true;		
		
		return false;
	}	
	
	/*
	 * calculate the index of the tile array to find the elevation value
	 * elevation values are stored as a signed 2 byte integer, in row major order. 
	 * the first row is the northernmost in the tile.
	 */
	public int getIndex() {
		// get the decimal points of latitude and longitude
		double lat = this.getLat(), lon = this.getLong();
		double deciLat, deciLon;
	
		// latitudes represents rows in a tile
		// "NORTH" - when latitude is in north the row is in reverse order and
		// the way I calculate the index is in reverse order which gives me the right order. 
		// "SOUTH" - When latitude is in south the row is in order and the way I calculate the index is in reverse order
		// so I calculate decimal values in reverse order which also gives me the right order.
		if(lat>=0){
			// (north)
			// ex. latitude = 22, deciLat = 0 or lat = 22.22, deciLat = 0.22 
			deciLat = lat - (int)lat; // get the decimal value
		} else { 
			// when latitude is negative (south)
			// ex. if latitude = -20.22, deciLat = -20.22 + 21 = 0.78
			//		  latitdue = -21, deciLat = 0			
			deciLat = lat + Math.abs(Math.floor(lat)); // reverse the decimal value!
		}
		
		// longitudes represent columns in order regardless of the direction in a tile
		// so just calculate the distance from the very west point to the east
		if(lon>=0){
			// ex. if longitude = 149.22, deciLon = 0.22 
			deciLon = lon - (int)lon; // get the decimal value
		} else{ 
			// when longitude is negative
			// ex. if longitude = -149.22, deciLon = -149.22 + 150 = 0.78
			// 		  long = -149, deciLon = -149 + 149 = 0			
			deciLon = lon + Math.abs(Math.floor(lon));
		}
		
		
		// round the row and column to the closest integer
		int row = (int)(deciLat * numOfElevPoint); // 0<= row < 1201
		int col = (int)(deciLon * numOfElevPoint); // 0<= column < 1201
		
		// Keep in mind that the first row is the northernmost
		// and tile files are named for their south west corner
		// multiply by the number of elevation point to skip each row 
		int index = (((numOfElevPoint - row) - 1) * numOfElevPoint + col);

		System.out.println("row and col "+ row + ","+ col + " index = " + index);
		
		return index;
	}
	
	public double getLat(){
		return this.latitude;
	}
	
	public double getLong(){
		return this.longitude;
	}
	
	public String toString(){
		return "Lat: " + getLat() + ", Lon: " + getLong();
	}

	
	
}
