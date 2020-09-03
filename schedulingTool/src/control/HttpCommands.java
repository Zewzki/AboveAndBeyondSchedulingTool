package control;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpCommands {

    private static final String geocodeURL = "https://open.mapquestapi.com/geocoding/v1/address?";
    private static final String staticMapURL = "https://wwww.mapquestapi.com/staticmap/V5/map?";
    private static final String apiKey = "GdCAz5hjHAlQN3lb9PFUabP0ElcW7BsF";

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

        String location = "&location=" + address.replace(" ", "%20") + "," + city + "," + state + "," + zip;
        String url = geocodeURL + "key=" + apiKey + location;

        //System.out.println(url);

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());

            int latStartIndex = response.body().lastIndexOf("\"lat\":") + 6;
            int lngStartIndex = response.body().lastIndexOf("\"lng\":") + 6;

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

    public static void getStaticMap(String center, String zoom, String size, String boundingBox, String margin) {

        String url = staticMapURL;
        url += "key=" + apiKey;
        //url += "&center=" + center;
        //url += "&zoom=" + zoom;
        //url += "&size=" + size;
        url += "&boundingBox=" + boundingBox;
        url += "&margin=" + margin;

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            System.err.println("Unable to process request: " + url);
            e.printStackTrace();
        }

    }

}
