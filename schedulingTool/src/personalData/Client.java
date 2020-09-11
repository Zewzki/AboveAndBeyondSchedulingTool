package personalData;

public class Client extends PersonalData{

    public Client(String firstName, String lastName, String address, String city, String state, String zip, float latitude, float longitude, PersonType type) {
        super(firstName, lastName, address, city, state, zip, latitude, longitude, type);
    }

    public Client(String firstName, String lastName, String address, String city, String state, String zip) {
        super(firstName, lastName, address, city, state, zip, PersonType.Client);
    }

}
