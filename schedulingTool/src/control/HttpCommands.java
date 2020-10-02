package control;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpCommands {

    private static final String geocodeURL = "https://www.mapquestapi.com/geocoding/v1/address?";
    private static final String staticMapURL = "https://www.mapquestapi.com/staticmap/v5/map?";
    private static final String apiKey = "GdCAz5hjHAlQN3lb9PFUabP0ElcW7BsF";
    private static final String defaultMap = staticMapURL + "key=" + apiKey + "&center=Minneapolis,MN";

    public static void request(String url) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            System.err.println("Unable to process request: " + url);
            e.printStackTrace();
        }

    }

    public static float[] getCoordinates(String address, String city, String state, String zip) {

        if(address.contains(" #")) address = address.split(" #")[0];

        String filteredAddress = address.replace(" ", "%20");
        filteredAddress = filteredAddress.replace("\"", "");
        String filteredCity = city.replace(" ", "%20");

        String location = "&location=" + filteredAddress + "," + filteredCity + "," + state + "," + zip;
        String url = geocodeURL + "key=" + apiKey + location;

        System.out.println(url);

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());

            int latStartIndex = response.body().indexOf("\"lat\":") + 6;
            int lngStartIndex = response.body().indexOf("\"lng\":") + 6;

            int latEndIndex = latStartIndex;
            char currChar = response.body().charAt(latEndIndex);
            while(currChar != ',' && currChar != '}') {
                latEndIndex++;
                currChar = response.body().charAt(latEndIndex);
            }

            int lngEndIndex = lngStartIndex;
            currChar = response.body().charAt(lngEndIndex);
            while(currChar != ',' && currChar != '}') {
                lngEndIndex++;
                currChar = response.body().charAt(lngEndIndex);
            }

            String latString = response.body().substring(latStartIndex, latEndIndex);
            String lngString = response.body().substring(lngStartIndex, lngEndIndex);

            float lat = Float.parseFloat(latString);
            float lng = Float.parseFloat(lngString);

            return new float[]{lat, lng};

        } catch (IOException | InterruptedException e) {
            System.err.println("Unable to process request: " + url);
            e.printStackTrace();
            return new float[2];
        }

    }

    public static BufferedImage getStaticMap(String center, String zoom, String size, String boundingBox, String margin) {

        String url = staticMapURL;
        url += "key=" + apiKey;
        url += "&size=" + size;
        //url += "&center=" + center;
        //url += "&zoom=" + zoom;
        url += "&boundingBox=" + boundingBox;
        //url += "&margin=" + margin;

        System.out.println(url);

        try {

            return ImageIO.read(new URL(url));

        } catch (IOException e) {
            System.err.println("Unable to process request: " + url);
            e.printStackTrace();
        }

        try {
            return ImageIO.read(new URL(defaultMap));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
