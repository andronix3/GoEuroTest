package com.goeuro.test;

// TODO hashCode() toString() and equals()
class GoEuroModel {
    int _id;
    String key;
    String name;
    String fullName;
    String iata_airportCode;
    String type;
    String country;

    GeoPosition geo_position;
    int location_id;
    boolean inEurope;
    String countryCode;
    Integer distance;

    @Override
    public String toString() {
	return "GoEuroModel [_id=" + _id + ", name=" + name + ", type=" + type + ", geo_position=" + geo_position + "]";
    }

}