package comp1206.sushi.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Postcode;

public class Postcode extends Model implements Serializable {

	private String name;
	private Map<String,Double> latLong;
	private Number distance;

	final double EARTH_RADIUS = 6378.137;

	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = 0;
	}

	public Postcode(){}
	
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant.getLocation());
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Number getDistance() {
		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		return this.latLong;
	}
	
	protected void calculateDistance(Postcode otherPoint) {
		//This function needs implementing
		Postcode destination = otherPoint;
//		this.distance = Integer.valueOf(0);
		double lat1 = getLatLong().get("lat");
		double lon1 = getLatLong().get("lon");
//		Number straightLineDistance = Math.sqrt((Math.pow(lat.doubleValue(),2) - Math.pow(destination.getLatLong().get("lat").doubleValue(), 2)) + (Math.pow(lon.doubleValue(), 2) - Math.pow(destination.getLatLong().get("lon").doubleValue(), 2)));
//		this.distance = 2 * (EARTH_RADIUS) * Math.cos((0.5 * straightLineDistance.doubleValue())/EARTH_RADIUS);
//		System.out.println(distance);
		double lat2 = destination.getLatLong().get("lat");
		double lon2 = destination.getLatLong().get("lon");

		double dLat  = Math.toRadians((lat2 - lat1));
		double dLong = Math.toRadians((lon2 - lon1));

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double c = 2 * Math.atan2(Math.sqrt(haversin(dLat) + Math.cos(lat1) * Math.cos(lat2) * haversin(dLong)), Math.sqrt(1 - haversin(dLat) + Math.cos(lat1) * Math.cos(lat2) * haversin(dLong)));
		this.distance =  EARTH_RADIUS * c;


	}

	private double haversin(double d){
		return Math.pow(Math.sin(d/2),2);
	}

	protected void calculateLatLong() {
		//This function needs implementing
		this.latLong = new HashMap<String,Double>();
		latLong.put("lat", 0d);
		latLong.put("lon", 0d);
		String websiteContents = null;
		URL url;
		boolean successfulGetRequest = false;
		try {

			url = new URL("https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode=" + this.name.replace(" ", ""));
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
			encoding = encoding == null ? "UTF-8" : encoding;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			String body = new String(baos.toByteArray(), encoding);
			websiteContents = body;
			successfulGetRequest = true;
			System.out.println(websiteContents);

			if (websiteContents.equalsIgnoreCase("{\"error\":\"Invalid format of postcode\"}") || websiteContents.equalsIgnoreCase("{\"error\":\"Postcode could not be resolved to a valid lat\\/long\"}")){
				successfulGetRequest = false;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if (successfulGetRequest){
			websiteContents = websiteContents.replace("\"","");
			websiteContents = websiteContents.replace("{", "");
			websiteContents = websiteContents.replace("}", "");
			websiteContents = websiteContents.replace("postcode:", "");
			websiteContents = websiteContents.replace("lat:", "");
			websiteContents = websiteContents.replace("long:", "");
			System.out.println(websiteContents);
			System.out.println(Double.parseDouble(websiteContents.split(",")[1]));
			System.out.println(Double.parseDouble(websiteContents.split(",")[2]));
			latLong.put("lat", Double.parseDouble(websiteContents.split(",")[1]));
			latLong.put("lon", Double.parseDouble(websiteContents.split(",")[2]));
		} else {
			System.out.println("COULD NOT GET DISTANCE");
		}

	}
	
}
