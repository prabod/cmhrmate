package com.emergelk.comeheremate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.telephony.PhoneNumberUtils.stripSeparators;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Map<String, String>> mPeopleList;

    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    private Button comeHereMate;

    public static String getCountryCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String numWithCode = Iso2Phone.getPhone(tm.getNetworkCountryIso());
        return numWithCode;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null if not registered
        if (currentUser != null) {
            setContentView(R.layout.activity_main);
            mPeopleList = new ArrayList<Map<String, String>>();
            PopulatePeopleList();
            mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.friendName);

            mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview, new String[]{"Name", "Phone", "Type"}, new int[]{R.id.ccontName, R.id.ccontNo, R.id.ccontType});

            mTxtPhoneNo.setAdapter(mAdapter);
            Log.d("user", currentUser.getUsername());
            mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> av, View arg1, int index,
                                        long arg3) {
                    Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                    String name = map.get("Name");
                    String number = map.get("Phone");
                    if (!number.startsWith("+")) {
                        String fnumber = stripSeparators(number).startsWith("0") ?
                                stripSeparators(number)
                                        .substring(1) : stripSeparators(number);
                        String formated = getCountryCode(MainActivity.this) + fnumber;
                        mTxtPhoneNo.setText("" + name + "<" + formated + ">");
                    } else
                        mTxtPhoneNo.setText("" + name + "<" + number + ">");
                }

            });
            comeHereMate = (Button) findViewById(R.id.comeHereButton);
            comeHereMate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userName = mTxtPhoneNo.getText().toString();
                    userName = userName.replaceAll("[^\\+0-9]", "");
                    Intent intent = new Intent(MainActivity.this, WaitForFriend.class);
                    intent.putExtra("uname", userName);
                    startActivity(intent);

                }
            });
        } else {
            promptLogin();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void promptLogin() {
        finish();
        Intent logIn = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logIn);
    }

    public void PopulatePeopleList() {

        mPeopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext()) {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else if (numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if (numberType.equals("2"))
                        NamePhoneType.put("Type", "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();

        startManagingCursor(people);
    }

}
