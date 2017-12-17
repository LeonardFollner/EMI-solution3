package de.mbranig.emiexercise3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.List;

public class EditContactActivity extends AppCompatActivity {

    private Contact contact;
    private List<Contact> contacts;
    private int contactPosition;

    private Button btnSave;

    private EditText etTitle, etFirstName, etLastName, etAddress, etZip, etCity, etCountry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        InitializeActivity();
    }

    private void InitializeActivity() {
        Intent i = getIntent();
        contact = (Contact) i.getSerializableExtra("contact");
        contactPosition = i.getIntExtra("contactPosition", 0);
        contacts = (List) i.getSerializableExtra("contacts");

        for(Contact c : contacts) {
            String contactName = contact.getTitle() + " " + contact.getFirstname() + " " + contact.getLastname();
            String cName = c.getTitle() + " " + c.getFirstname() + " " + c.getLastname();
            if(contactName.equals(cName)) {
                contact = c;
            }
        }

        btnSave = (Button) findViewById(R.id.buttonSave);

        etTitle = (EditText) findViewById(R.id.editTextTitle);
        etFirstName = (EditText) findViewById(R.id.editTextFirstName);
        etLastName = (EditText) findViewById(R.id.editTextLastName);
        etAddress = (EditText) findViewById(R.id.editTextAddress);
        etZip = (EditText) findViewById(R.id.editTextZip);
        etCity = (EditText) findViewById(R.id.editTextCity);
        etCountry = (EditText) findViewById(R.id.editTextCountry);

        etTitle.setText(contact.getTitle());
        etFirstName.setText(contact.getFirstname());
        etLastName.setText(contact.getLastname());
        etAddress.setText(contact.getAddress());
        etZip.setText(contact.getZip());
        etCity.setText(contact.getCity());
        etCountry.setText(contact.getCountry());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveChanges();
            }
        });
    }

    private void SaveChanges() {

        contact.setTitle(etTitle.getText().toString());
        contact.setFirstname(etFirstName.getText().toString());
        contact.setLastname(etLastName.getText().toString());
        contact.setAddress(etAddress.getText().toString());
        contact.setZip(etZip.getText().toString());
        contact.setCity(etCity.getText().toString());
        contact.setCountry(etCountry.getText().toString());

        Intent i = new Intent(this, ContactManagementActivity.class);
        i.putExtra("contactPosition", contactPosition);
        i.putExtra("contacts", (Serializable)contacts);
        //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        setResult(RESULT_OK, i);
        finish();
    }

}
