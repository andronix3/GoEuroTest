package com.goeuro.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.NumberFormat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoEuroMain {

    private static final String endPoint = "http://api.goeuro.com/api/v2/position/suggest/en/";
    private static final String path = System.getProperty("user.home");
    private static final String filename = "GoEuro.csv";

    public static void main(String[] args) throws IOException {
	new GoEuroMain().processRequest(args[0]);
    }

    public void processRequest(String city)
	    throws MalformedURLException, IOException, ProtocolException, UnsupportedEncodingException {
	String s = endPoint + city;
	URL url = new URL(s);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	conn.setDoOutput(true);
	conn.setDoInput(true);
	conn.setUseCaches(false);
	conn.setRequestMethod("GET");

	int responseCode = conn.getResponseCode();

	if (responseCode == 200) {
	    String response = readResponse(conn);
	    String sb = createCSV(response);
	    save(sb);
	}
	conn.disconnect();
    }

    private String readResponse(HttpURLConnection conn) throws IOException, UnsupportedEncodingException {
	InputStream in = conn.getInputStream();
	String encoding = conn.getContentEncoding() == null ? "UTF-8" : conn.getContentEncoding();

	Reader reader = new InputStreamReader(in, encoding);
	CharBuffer target = CharBuffer.allocate(20000);
	while (reader.ready()) {
	    reader.read(target);
	}
	target.flip();
	final String toString = target.toString();
	return toString;
    }

    private String createCSV(final String toString) {
	StringBuilder sb = new StringBuilder();
	String delimiter = ";";
	String[] columns = { "_id", "name", "type", "latitude", "longitude" };
	for (String col : columns) {
	    sb.append(col).append(delimiter);
	}
	sb.append("\n");

	NumberFormat nf = NumberFormat.getInstance();
	
	
	JsonElement element = new JsonParser().parse(toString);
	if (element.isJsonArray()) {
	    JsonArray jsonArray = element.getAsJsonArray();
	    jsonArray.forEach(t -> {
		JsonObject jsonObject = t.getAsJsonObject();
		Gson gson = new Gson();
		GoEuroModel fromJson = gson.fromJson(jsonObject, GoEuroModel.class);
		System.out.println(fromJson);
		sb.append(fromJson._id).append(delimiter);
		sb.append(fromJson.name).append(delimiter);
		sb.append(fromJson.type).append(delimiter);
		sb.append(nf.format(fromJson.geo_position.latitude)).append(delimiter);
		sb.append(nf.format(fromJson.geo_position.longitude)).append(delimiter);
		sb.append("\n");
	    });
	}
	return sb.toString();
    }

    private void save(String sb) throws IOException, FileNotFoundException {
	File f = new File(path, filename);

	try (FileOutputStream out = new FileOutputStream(f)) {
	    try (Writer wr = new OutputStreamWriter(out)) {
		wr.write(sb);
	    }
	}
    }
}
