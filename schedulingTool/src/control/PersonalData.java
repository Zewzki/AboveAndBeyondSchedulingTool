package control;

public class PersonalData {

    private static final float distanceCoefficient = .0001f;

    public enum PersonType {Client, Therapist};

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private float latitude;
    private float longitude;
    private int translationX;
    private int translationY;

    private PersonType type;

    public PersonalData(String firstName, String lastName, String address, String city, String state, String zip, float latitude, float longitude, PersonType type) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;

    }

    public PersonalData(String firstName, String lastName, String address, String city, String state, String zip, PersonType type) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.type = type;

        float[] coords = HttpCommands.getCoordinates(address, city, state, zip);
        this.latitude = coords[0];
        this.longitude = coords[1];

    }

    public void calculateTranslation(float minLat, float maxLat, float minLng, float maxLng, int screenWidth, int screenHeight) {

        //[maxLat, minLng, minLat, maxLng]
        // lat - bb[1] / bb[3] - bb[1] * screenWidth

        translationX = (int) ((longitude - minLng) / (maxLng - minLng) * screenWidth);
        translationY = (int) ((latitude - maxLat) / (minLat - maxLat) * screenHeight);

        int distX = translationX - (screenWidth / 2);
        int distY = translationY - (screenHeight / 2);

        double distFromCenter = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

    }

    public void setTranslation(int x, int y) {
        translationX = x;
        translationY = y;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public int getTranslationX() { return translationX; }
    public int getTranslationY() { return translationY; }
    public PersonType getType() { return type; }

    public String toString() {

        String s = "";
        s += type.name() + ": " + firstName + " " + lastName;
        s += "\n" + address + ", " + city + " " + state + " " + zip;
        s += "\n(" + latitude + "," + longitude + ") -> (" + translationX + ", " + translationY + ")\n";

        return s;

    }

}
