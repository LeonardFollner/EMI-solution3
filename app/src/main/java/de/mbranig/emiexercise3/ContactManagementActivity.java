package de.mbranig.emiexercise3;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.thoughtworks.xstream.XStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedInputStream;

public class ContactManagementActivity extends AppCompatActivity {

    private Spinner spContacts;
    private TextView
            tvTitle,
            tvFirstName,
            tvLastName,
            tvAddress,
            tvZip,
            tvCity,
            tvCountry;


    private Button btnEdit, btnShare;

    private List<Contact> contacts;
    private Contact editableContact;

    private int selectedItemId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_management);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.tuHausfarbeBlauDunkel)));

        InitializeApp();
    }

    /**
     * Construct the Interactive Structure
     */
    private void InitializeApp() {
        spContacts = (Spinner) findViewById(R.id.spinnerContacts);

        tvTitle = (TextView) findViewById(R.id.textViewTitle);
        tvFirstName = (TextView) findViewById(R.id.textViewFirstName);
        tvLastName = (TextView) findViewById(R.id.textViewLastName);
        tvAddress = (TextView) findViewById(R.id.textViewAddress);
        tvZip = (TextView) findViewById(R.id.textViewZip);
        tvCity = (TextView) findViewById(R.id.textViewCity);
        tvCountry = (TextView) findViewById(R.id.textViewCountry);

        btnEdit = (Button) findViewById(R.id.buttonEdit);
        btnShare = (Button) findViewById(R.id.buttonShare);

        /*
        FILL CONTACTS
         */

        ArrayList<String> contactNames = new ArrayList<>();

        // IMPORTANT: Fill the list with at least one item. Else, the spinner could look empty for some reason.
        contactNames.add("Test Entry 1");
        contactNames.add("Test Entry 2");
        contactNames.add("Test Entry 3");
        contactNames.add("Test Entry 4");
        contactNames.add("Test Entry 5");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, contactNames);

        spContacts.setAdapter(adapter);


        spContacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(view instanceof TextView))
                    return;

                TextView item = (TextView) view;
                String selectedName = item.getText().toString();

                Contact current = GetContactByName(selectedName);
                DisplayContactDetails(current);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        /*
        Herr Robert Meyer
        Apfelbergstraße 10
        9430 St. Margrethen
        Schweiz

        Frau Elisabeth Stramm
        Fritz-Konzert-Straße 1a
        6020 Innsbruck
        Österreich

        Herr Stefan Wennige
        Kirchplatz 13
        6112 Wattens
        Österreich

        Frau Ella Beckmann
        Falkenstraße 3
        01067 Dresden
        Deutschland

        Frau Anne Watson
        1 Langdon Park Rd
        London N6 5PS
        Vereinigtes Königreich
         */

        contacts = new ArrayList<>();
        contacts.add(new Contact("Herr", "Robert", "Meyer", "Apfelbergstraße 10", "9430", "St. Margrethen", "Schweiz"));
        contacts.add(new Contact("Frau", "Elisabeth", "Stramm", "Fritz-Konzert-Straße 1a", "6020", "Innsbruck", "Österreich"));
        contacts.add(new Contact("Herr", "Stefan", "Wennige", "Kirchplatz 13", "6112", "Wattens", "Österreich"));
        contacts.add(new Contact("Frau", "Ella", "Beckmann", "Falkenstraße 3", "01067", "Dresden", "Deutschland"));
        contacts.add(new Contact("Frau", "Anne", "Watson", "1 Langdon Park Rd", "N6 5PS", "London", "Vereinigtes Königreich"));

        /*
        LOAD FILE WITH SAVED CONTACTS DATA
         */

        LoadContactsFromFile();

        contactNames.clear();
        for (Contact c : contacts) {
            contactNames.add(c.getTitle() + " " + c.getFirstname() + " " + c.getLastname());
        }

        /*
        ADD BUTTON INTERACTIVITY
         */

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartExternalEditing();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareContactDetails();
            }
        });

        /*
        If we got a new updated contact, we should include this
         */
        Intent i = getIntent();
        if(i.hasExtra("contacts")) {
            contacts = (List)i.getSerializableExtra("contacts");
            UpdateSpinner();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            /*
            If we got a new updated contact, we should include this
             */
            if(data.hasExtra("contacts")) {
                contacts = (List)data.getSerializableExtra("contacts");
                UpdateSpinner();
            }
        }


        spContacts.setSelection(data.getIntExtra("contactPosition", 0), true);
        Contact current = GetContactByName(spContacts.getSelectedItem().toString());
        DisplayContactDetails(current);
    }


    /**
     * Invoke the Share Intent to provide address info to other apps
     */
    private void ShareContactDetails() {
        Contact c = GetContactByName(((TextView)spContacts.getSelectedView()).getText().toString());
        String output = "";
        output += c.getTitle() + " " + c.getFirstname() + " " + c.getLastname() + "\n";
        output += c.getAddress() + "\n";
        output += c.getZip() + " " + c.getCity() + "\n";
        output += c.getCountry();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, output);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * Returns the Contact object based on the contact's name
     * @param name Contact name of the form: <br />
     *             <code>Title + " " + Firstname + " " + Lastname</code>
     * @return Contact object if exists
     */
    private Contact GetContactByName(String name) {
        Contact current = contacts.get(0);
        for(Contact c : contacts) {
            if(name.equals(c.getTitle() + " " + c.getFirstname() + " " + c.getLastname())) {
                current = c;
            }
        }
        return current;
    }

    /**
     * Set the displayed contact info by contact name
     * @param contact
     */
    private void DisplayContactDetails(Contact contact) {
        Contact current = contact;

        tvTitle.setText(current.getTitle());
        tvFirstName.setText(current.getFirstname());
        tvLastName.setText(current.getLastname());
        tvAddress.setText(current.getAddress());
        tvZip.setText(current.getZip());
        tvCity.setText(current.getCity());
        tvCountry.setText(current.getCountry());
    }


    /**
     * Update the spinner list
     */
    private void UpdateSpinner() {
        /*
        SAVE CURRENT CONTACTS LIST TO FILE
         */

        StoreContactsToFile();

        if(!(spContacts.getAdapter() instanceof ArrayAdapter)) return;

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spContacts.getAdapter();
        adapter.clear();
        for (Contact c : contacts) {
            adapter.add(c.getTitle() + " " + c.getFirstname() + " " + c.getLastname());
        }
    }

    /**
     * Initialises external editing
     */
    private void StartExternalEditing() {
        Contact contact = GetContactByName(((TextView)spContacts.getSelectedView()).getText().toString());
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contact", contact);
        i.putExtra("contactPosition", spContacts.getSelectedItemPosition());
        i.putExtra("contacts", (Serializable)contacts);
        startActivityForResult(i, 0);
    }

    /**
     * Loads the contact information from file, if existing
     */
    private void LoadContactsFromFile() {
        // Load file input stream
        // Do nothing, if the file is not there or empty
        String data = readFromFile();
        if(data == null || data.isEmpty()) return;

        // Parse file data
        XStream xstream = new XStream();
        List<Contact> contacts = (List<Contact>)xstream.fromXML(data);

        // Store pared data to contacts list
        this.contacts = contacts;
    }

    /**
     * Stores contact details to file (no matter what)
     */
    private void StoreContactsToFile() {
        // Serialise contacts to XML File
        XStream xstream = new XStream();
        String xml = xstream.toXML(contacts);

        // Store data to file (override if necessary)
        writeToFile(xml);
    }

    /**
     * Stores a String into the contacts.xml file
     * @param data Input String object
     */
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("contacts.xml", this.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            // nothing
        }
    }

    /**
     * Reads the contacts.xml into a String
     * @return target String
     */
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("contacts.xml");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            // nothing
        } catch (IOException e) {
            // nothing
        }

        return ret;
    }

}
