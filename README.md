# SRTM3 Data Parser 
To get the height above the ground we can lookup a global terrain database (SRTM3)
to first establish the terrain elevation, and then subtract that from the GPS altitude to
determine the altitude of the aircraft above ground level.

## Inputs
A folder of elevation tiles files: Elevation tile files are named for their south west corner:
“N006E160.HGT”. A single tile represents a 1x1 degree area; the files are binary and
elevations are stored as a signed 2 byte integer, in row major order. There are 1201 elevation
points per side of the tile, meaning a single tile is 2,884,802 bytes in size. <br><br>

A flightpath: This is an simple array of lat/long coordinates, the lat longs are a struct with
double precision floats for lat and long. Latitude is the north/south direction, longitude is the
west/east direction. Positive latitude is north and positive longitude is east.

## Outputs
An array of terrain elevations in metres for each point in the flightpath.
