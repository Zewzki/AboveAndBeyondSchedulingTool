package personalData;

import java.util.ArrayList;

public class Therapist extends PersonalData {

    private ArrayList<Client> clientList;

    private boolean displayClients;

    public Therapist(String firstName, String lastName, String address, String city, String state, String zip, float latitude, float longitude, PersonType type) {
        super(firstName, lastName, address, city, state, zip, latitude, longitude, type);

        displayClients = false;
        clientList = new ArrayList<>();

    }

    public Therapist(String firstName, String lastName, String address, String city, String state, String zip) {
        super(firstName, lastName, address, city, state, zip, PersonType.Therapist);

        displayClients = false;
        clientList = new ArrayList<>();

    }

    public void addClient(Client c) {
        clientList.add(c);
    }

    public ArrayList<Client> getClientList() { return clientList; }
    public boolean getDisplayClients() { return displayClients; }
    public void toggleDisplayClients() { displayClients = !displayClients; }

    public String toString() {

        String s = super.toString() + "Clients: ";

        for(Client c : clientList) s += c.getFirstName() + " " + c.getLastName() + ", ";
        s += "\n";

        return s;

    }

}
