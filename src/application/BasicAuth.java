package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BasicAuth {
	
	
	public void sample() throws IOException{
		String webPage = "http://g-services-qa1.copart.com/reference-ws/reference/vehicleTypes?view=VehicleFinder&category=ALL";
		String login = "cobalt";
		String password = "38JW0fMi9iXVJ0sm14n04v7BC8EBkm9P4L5gfctJYnO4vybQmO";

		URL url;
		
			url = new URL(webPage);
		
			// TODO Auto-generated catch block
			
		
		String loginPassword = login+ ":" + password;
		//String encoded = new sun.misc.BASE64Encoder().encode (loginPassword.getBytes());
		URLConnection conn = url.openConnection();
		//conn.setRequestProperty ("Authorization", "Basic " + encoded);
	}

	public static void main(String[] args) throws IOException {

		//reloadTomcatWebApplication("cobalt","38JW0fMi9iXVJ0sm14n04v7BC8EBkm9P4L5gfctJYnO4vybQmO","http://g-services-qa1.copart.com/reference-ws/reference/makes?view=VehicleFinder&category=ALL",true);
		//String json= "";
	readJSON();
	}
	
	public static void readJSON() throws IOException{
		//Scanner abc =  new Scanner(new File("output.json"));
		String content = new Scanner(new File("src/application/jsonfile.json")).useDelimiter("\\Z").next();
		System.out.println(content);
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new InputStreamReader(new FileInputStream("src/application/jsonfile.json")));

			JSONObject jsonObject = (JSONObject) obj;

			String name = (String) jsonObject.get("name");
			System.out.println(name);

			long age = (Long) jsonObject.get("age");
			System.out.println(age);

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get("value");
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

	     

		
		
	}
	public static String reloadTomcatWebApplication(String user, String pwd, String urlWithParameters, boolean returnResponse) {
	    URL url = null;
	    try {
	        url = new URL(urlWithParameters);
	    } catch (MalformedURLException e) {
	        System.out.println("MalformedUrlException: " + e.getMessage());
	        e.printStackTrace();
	        return "-1";
	    }

	    URLConnection uc = null;
	    try {
	        uc = url.openConnection();
	    } catch (IOException e) {
	        System.out.println("IOException: " + e.getMessage());
	        e.printStackTrace();
	        return "-12";
	    }


	    String userpass = user + ":" + pwd;
	    String basicAuth = "Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJlbnRpdHlfdHlwZSI6Im1lbWJlciIsImVudGl0eV9uYW1lIjoic3BhaW40NiB0ZXN0IiwidXNlcl9uYW1lIjoic3BhaW40NkBtYWlsaW5hdG9yLmNvbSIsIm1lbWJlcl9zdGF0dXMiOiJDUFJURVM6QSIsInNjb3BlIjpbImxvZ2luIl0sImV4cCI6MTQ2NjAyNDA4OSwiZW50aXR5X2lkIjo3MDAwMjAxMDAsImp0aSI6IjgxN2NiYTViLTg3ZGMtNDJlMy05MzRlLWZjNzFhYjYxMjUxMyIsImNsaWVudF9pZCI6ImNvYmFsdCJ9.Ffko-FK7htwMBhW7kXU0sIgfCXxP8g70NSz394wFwaRpWGHIj4dx9jBQbk9cKPwHPGNWq24KEv3eloBpkh-r6zrPIS5WR_elBFkSu2s2fySl7fzUUcIHSWRmMCR1AvcKAj0OtrOkX4QsY_BsqTwy23kx_1tG3zIWydBANnbrCfoS1aTWGqsUXolnDJVwciDSxt0d3n3iKAQHgx0MAUwyT3PvhgmMGn1kOt7KCEvoPTnZK8G5Nt__tSD4TGfujCwjDL0WiOlYxbpmOpmeFqUYmDSegg78BKxgv0hQozaktrraEwtP1fmzZKTCLCLKPJrX4RQ3W9eBgkB50WBw4mQD1Q";
	  
	    uc.setRequestProperty("site", "CPRTES");
		uc.setRequestProperty("language", "es");
		uc.setRequestProperty("country", "ES");
	    uc.setRequestProperty("Authorization", basicAuth);
	    InputStream is = null;
	    try {
	        is = uc.getInputStream();
	    } catch (IOException e) {
	        System.out.println("IOException: " + e.getMessage());
	        e.printStackTrace();
	        return "-13";
	    }
	    if (returnResponse) {
	        BufferedReader buffReader = new BufferedReader(new InputStreamReader(is));
	        StringBuffer response = new StringBuffer();

	        String line = null;
	        try {
	            line = buffReader.readLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "-1";
	        }
	        while (line != null) {
	            response.append(line);
	            response.append('\n');
	            try {
	                line = buffReader.readLine();
	            } catch (IOException e) {
	                System.out.println(" IOException: " + e.getMessage());
	                e.printStackTrace();
	                return "-14";
	            }
	        }
	        try {
	            buffReader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "-15";
	        }
	        System.out.println("Response: " + response.toString());
	        return response.toString();
	    }
	    return "0";
	}

}