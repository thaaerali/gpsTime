package com.kumailn.prayertime;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "com.kumailn.prayertime";
    public static final String defaultMethod = "0";
    PendingIntent dynamicFajrPendingIntent;
    PendingIntent dynamicAsrPendingIntent;
    PendingIntent dynamicDhurPendingIntent;
    PendingIntent dynamicMaghribPendingIntent;
    PendingIntent dynamicIshaPendingIntent;


    PendingIntent pendingIntent;
    PendingIntent pendingIntent2;
    PendingIntent pendingIntent3;
    PendingIntent pendingIntent4;
    PendingIntent pendingIntent5;
    PendingIntent fajr2Pending;
    PendingIntent fajr3Pending;
    PendingIntent fajr4Pending;
    PendingIntent fajr5Pending;
    PendingIntent fajr6Pending;
    PendingIntent fajr7Pending;
    PendingIntent dhur2Pending;
    PendingIntent dhur3Pending;
    PendingIntent dhur4Pending;
    PendingIntent dhur5Pending;
    PendingIntent dhur6Pending;
    PendingIntent dhur7Pending;
    PendingIntent isha2Pending;
    PendingIntent isha3Pending;
    PendingIntent isha4Pending;
    PendingIntent isha5Pending;
    PendingIntent isha6Pending;
    PendingIntent isha7Pending;
    PendingIntent testPendingIntent1;

    AlarmManager alarm_manager;
    private Switch mySwitch;

    public static boolean aSent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Toolbar setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        mySwitch = (Switch)findViewById(R.id.switch1);

        try{
            //tries to set switch to saved position
            mySwitch.setChecked(Boolean.valueOf(loadDaylight()));
        }
        catch (Exception e){
        }

        if (loadNumericInstance() == 1){
            mySwitch.setChecked(TimeZone.getDefault().inDaylightTime( new Date() ));
            saveDaylight(true);
            Log.e(TAG, "Switch saved as on");
        }

        Log.e(String.valueOf(TimeZone.getDefault().inDaylightTime( new Date() )), "DAYLIGHTON?");

        //Saves state of switch
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mySwitch.isChecked()){
                    //Toast.makeText(Main2Activity.this, "works", Toast.LENGTH_SHORT).show();
                    saveDaylight(true);
                    Log.e(TAG, "Switch saved as on");
                    Log.wtf("Testing", "123");
                }
                else {
                    saveDaylight(false);
                    Log.e(TAG, "Switch saved as off");
                }
            }
        });

        Button aB = (Button)findViewById(R.id.alarmButton);
        Button offButton = (Button)findViewById(R.id.alarmOffButon);
        Button setHomeButton = (Button)findViewById(R.id.setHomeButton);

        //Alarm manager object initialization
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);


        //Spinner object setup
        final Spinner mySpin = (Spinner)findViewById(R.id.myspin1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpin.setAdapter(myAdapter);
        mySpin.setSelection(loadDat());
        mySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Shia
                        save(0);
                        mySpin.setSelection(0);
                        break;
                    case 1:
                        //Karachi
                        save(1);
                        mySpin.setSelection(1);
                        break;
                    case 2:
                        //ISNA
                        save(2);
                        mySpin.setSelection(2);
                        break;
                    case 3:
                        //MWL
                        save(3);
                        mySpin.setSelection(3);
                        break;
                    case 4:
                        //Makkah
                        save(4);
                        mySpin.setSelection(4);
                        break;
                    case 5:
                        //Egypt
                        save(5);
                        mySpin.setSelection(5);
                        break;
                    case 6:
                        //Tehran
                        save(6);
                        mySpin.setSelection(6);
                        break;
                    case 7:
                        save(7);
                        mySpin.setSelection(7);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Save location of Home onClick (Raw coordinates)
        setHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHomeLocation(loadLat(), loadLon());
                Toast.makeText(Main2Activity.this, "Home location successfully saved", Toast.LENGTH_LONG).show();
                Log.e("HOME SET: ", loadHomeLocation().get(0) + " " + loadHomeLocation().get(1));
            }
        });
        //Alarm On onclick listner
        aB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alarmState = loadAlarm();
                if(alarmState == "on"){
                    return;
                }
                else if(alarmState == null){
                    return;
                }

                //Initialize dynamic alarm intents
                Intent dynamicFajrIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicFajrIntent.putExtra("Prayer", "Fajr").putExtra("Type", "Dynamic");
                Intent dynamicDhurIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicDhurIntent.putExtra("Prayer", "Dhur").putExtra("Type", "Dynamic");
                Intent dynamicAsrIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicAsrIntent.putExtra("Prayer", "Asr").putExtra("Type", "Dynamic");
                Intent dynamicMaghribIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicMaghribIntent.putExtra("Prayer", "Maghrib").putExtra("Type", "Dynamic");
                Intent dynamicIshaIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicIshaIntent.putExtra("Prayer", "Isha").putExtra("Type", "Dynamic");

                //Initialize pending intents
                dynamicFajrPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 101, dynamicFajrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                dynamicDhurPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 102, dynamicDhurIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                dynamicAsrPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 103, dynamicAsrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                dynamicMaghribPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 104, dynamicMaghribIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                dynamicIshaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 105, dynamicIshaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Initalize date format
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/HH/mm");
                Calendar cal2 = Calendar.getInstance();
                String myday = (dateFormat.format(cal2.getTime()));

                //Current Time variables
                int currentDay = Integer.parseInt(myday.split("/")[0]);
                int currentMonth = Integer.parseInt(myday.split("/")[1]);
                int currentYear = Integer.parseInt(myday.split("/")[2]);
                int currentHour = Integer.parseInt(myday.split("/")[3]);
                int currentMin = Integer.parseInt(myday.split("/")[4]);

                TimeZone tz1 = TimeZone.getDefault();
                int offset = tz1.getRawOffset()/1000/60/60;

                //Load longitute and latitute from saved data
                double latitude = Double.parseDouble(loadLat());
                double longitude = Double.parseDouble(loadLon());

                //Load daylight-savings
                Boolean myB = Boolean.valueOf(loadDaylight());
                double timezone = offset;

                //Adjust timezone based on daylight savings
                if(myB == null){
                    timezone = offset;
                }
                else if(myB == false){
                    timezone = offset;
                }
                else{
                    timezone = timezone + 1;
                }

                //Initialize praytime object
                PrayTime prayers = new PrayTime();
                prayers.setTimeFormat(prayers.Time24);
                prayers.setCalcMethod(loadDat());
                prayers.setAsrJuristic(prayers.Shafii);
                prayers.setAdjustHighLats(prayers.AngleBased);
                int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);

                Date now = new Date();
                Calendar todaysCalendar = Calendar.getInstance();
                Calendar tomorrowCalendar = Calendar.getInstance();
                tomorrowCalendar.set(currentYear, currentMonth - 1, currentDay + 1);
                todaysCalendar.setTime(now);

                //List of prayertimes for today
                ArrayList<String> prayerTimes = prayers.getPrayerTimes(todaysCalendar, latitude, longitude, timezone);
                //Prayer times for tomorrow
                ArrayList<String> prayerTimes2 = prayers.getPrayerTimes(tomorrowCalendar, latitude, longitude, timezone);
                //0 = Fajr, 1 = Sunrise, 2 = Dhur, 3 = Asr, 4 = Sunset, 5 = Maghrib, 6 = Isha
                ArrayList<String> prayerNames = prayers.getTimeNames();

                //Log to logCat
                Log.e("geo: " + loadLat(), loadLon());
                Log.e(prayerTimes.get(0) + " Today ", Integer.toString(loadDat()) + prayerNames.get(0));
                Log.e(prayerNames.get(0), "0AAAAAAAAA");
                Log.e(prayerNames.get(1), "1AAAAAAAAA");
                Log.e(prayerNames.get(2), "2AAAAAAAAA");
                Log.e(prayerNames.get(3), "3AAAAAAAAA");
                Log.e(prayerNames.get(4), "4AAAAAAAAA");
                Log.e(prayerNames.get(5), "5AAAAAAAAA");
                Log.e(prayerNames.get(6), "6AAAAAAAAA");

                //Initialize Gregorian Calendars from prayertime data
                GregorianCalendar myCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, currentHour, currentMin);
                GregorianCalendar fajrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(0).split(":")[0]), Integer.parseInt(prayerTimes.get(0).split(":")[1]));
                GregorianCalendar dhurCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(2).split(":")[0]), Integer.parseInt(prayerTimes.get(2).split(":")[1]));
                GregorianCalendar asrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(3).split(":")[0]), Integer.parseInt(prayerTimes.get(3).split(":")[1]));
                GregorianCalendar maghribCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(5).split(":")[0]), Integer.parseInt(prayerTimes.get(5).split(":")[1]));
                GregorianCalendar ishaCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(6).split(":")[0]), Integer.parseInt(prayerTimes.get(6).split(":")[1]));
                //Tomorrow
                GregorianCalendar fajrCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(0).split(":")[0]), Integer.parseInt(prayerTimes2.get(0).split(":")[1]));
                GregorianCalendar dhurCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(2).split(":")[0]), Integer.parseInt(prayerTimes2.get(2).split(":")[1]));
                GregorianCalendar maghribCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(5).split(":")[0]), Integer.parseInt(prayerTimes2.get(5).split(":")[1]));

                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent2);
                //Intent testIntent = new Intent(Main2Activity.this, prayerReceiver.class);
                //testPendingIntent1 =  PendingIntent.getBroadcast(Main2Activity.this, 2, testIntent, 0);
                //sendBroadcast(testIntent);

                //test
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, dynamicMaghribPendingIntent);

                if(System.currentTimeMillis() < fajrCal.getTimeInMillis()){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                    Log.e("FajrTodaySet:",String.valueOf(fajrCal.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal.get(Calendar.MONTH)+1)  + "/" +String.valueOf(fajrCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal.get(Calendar.MINUTE)));
                }
                else{
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                    Log.e("FajrTomorrowSet:",String.valueOf(fajrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal2.get(Calendar.MONTH)+1)  + "/" +String.valueOf(fajrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal2.get(Calendar.MINUTE)));
                }

                if(System.currentTimeMillis() < dhurCal.getTimeInMillis()){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                    Log.e("DhurTodaySet:",String.valueOf(dhurCal.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal.get(Calendar.MINUTE)));
                }
                else{
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                    Log.e("DhurTomorrowSet:",String.valueOf(dhurCal2.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal2.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal2.get(Calendar.MINUTE)));
                }

                if(System.currentTimeMillis() < maghribCal.getTimeInMillis()){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                    Log.e("MaghribTodaySet:",String.valueOf(maghribCal.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal.get(Calendar.MINUTE)));
                }
                else{
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                    Log.e("MaghribTomorrowSet:",String.valueOf(maghribCal2.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal2.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal2.get(Calendar.MINUTE)));
                }


                Intent i2 = new Intent(Main2Activity.this, Alarm_Receiver.class);
                i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                Intent i3 = new Intent(Main2Activity.this, Alarm_Receiver.class);
           /*     //Pending intent for each prayer notification because the time changes everyday - need a better solution.
                pendingIntent = PendingIntent.getBroadcast(Main2Activity.this, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent2 = PendingIntent.getBroadcast(Main2Activity.this, 1, i3, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent3 = PendingIntent.getBroadcast(Main2Activity.this, 2, i2, 0);
                pendingIntent4 = PendingIntent.getBroadcast(Main2Activity.this, 3, i2, 0);
                pendingIntent5 = PendingIntent.getBroadcast(Main2Activity.this, 4, i2, 0);
                fajr2Pending = PendingIntent.getBroadcast(Main2Activity.this, 5, i2, 0);
                fajr3Pending = PendingIntent.getBroadcast(Main2Activity.this, 6, i2, 0);
                fajr4Pending = PendingIntent.getBroadcast(Main2Activity.this, 7, i2, 0);
                fajr5Pending = PendingIntent.getBroadcast(Main2Activity.this, 8, i2, 0);
                fajr6Pending = PendingIntent.getBroadcast(Main2Activity.this, 9, i2, 0);
                fajr7Pending = PendingIntent.getBroadcast(Main2Activity.this, 10, i2, 0);
                dhur2Pending = PendingIntent.getBroadcast(Main2Activity.this, 11, i2, 0);
                dhur3Pending = PendingIntent.getBroadcast(Main2Activity.this, 12, i2, 0);
                dhur4Pending = PendingIntent.getBroadcast(Main2Activity.this, 13, i2, 0);
                dhur5Pending = PendingIntent.getBroadcast(Main2Activity.this, 14, i2, 0);
                dhur6Pending = PendingIntent.getBroadcast(Main2Activity.this, 15, i2, 0);
                dhur7Pending = PendingIntent.getBroadcast(Main2Activity.this, 16, i2, 0);
                isha2Pending = PendingIntent.getBroadcast(Main2Activity.this, 17, i2, 0);
                isha3Pending = PendingIntent.getBroadcast(Main2Activity.this, 18, i2, 0);
                isha4Pending = PendingIntent.getBroadcast(Main2Activity.this, 19, i2, 0);
                isha5Pending = PendingIntent.getBroadcast(Main2Activity.this, 20, i2, 0);
                isha6Pending = PendingIntent.getBroadcast(Main2Activity.this, 21, i2, 0);
                isha7Pending = PendingIntent.getBroadcast(Main2Activity.this, 22, i2, 0);*/

/*
                Log.e("CURRENTTIME::", String.valueOf(currentDay) + " " + String.valueOf(currentMonth) + " " + String.valueOf(currentYear) + ":" + String.valueOf(currentHour) + " " + String.valueOf(currentMin));
*/

                Calendar cal3 = Calendar.getInstance();
                cal3.set(currentYear, currentMonth - 1, currentDay + 2);

                Calendar cal4 = Calendar.getInstance();
                cal4.set(currentYear, currentMonth - 1, currentDay + 3);

                Calendar cal5 = Calendar.getInstance();
                cal5.set(currentYear, currentMonth - 1, currentDay + 4);

                Calendar cal6 = Calendar.getInstance();
                cal6.set(currentYear, currentMonth - 1, currentDay + 5);

                Calendar cal7 = Calendar.getInstance();
                cal7.set(currentYear, currentMonth - 1, currentDay + 6);

                Log.e("LATLON!: ", String.valueOf(latitude) + " " + String.valueOf(longitude));



                //Prayer times for 2 days from now
                ArrayList<String> prayerTimes3 = prayers.getPrayerTimes(cal3,
                        latitude, longitude, timezone);
                //Three days from now
                ArrayList<String> prayerTimes4 = prayers.getPrayerTimes(cal4,
                        latitude, longitude, timezone);
                //Four days from now
                ArrayList<String> prayerTimes5 = prayers.getPrayerTimes(cal5,
                        latitude, longitude, timezone);
                //Five days from now
                ArrayList<String> prayerTimes6 = prayers.getPrayerTimes(cal6,
                        latitude, longitude, timezone);
                //6 days from now
                ArrayList<String> prayerTimes7 = prayers.getPrayerTimes(cal7,
                        latitude, longitude, timezone);




                StringBuilder sb = new StringBuilder();
                for(String str : prayerTimes){
                    sb.append(str).append(";"); //separating contents using semi colon
                }

                String strfromArrayList = sb.toString();

                Log.e("ARRAYLIST: ", strfromArrayList);

                int testNum = 0;

         /*       Log.e(prayerTimes2.get(testNum) + " tom ", Integer.toString(loadDat()) + prayerNames.get(testNum));
                Log.e(prayerTimes3.get(testNum) + " tom2 " , Integer.toString(loadDat()) + prayerNames.get(testNum));
                Log.e(prayerTimes4.get(testNum) + " tom3 ", Integer.toString(loadDat()) + prayerNames.get(testNum));
                Log.e(prayerTimes5.get(testNum) + " tom4 ", Integer.toString(loadDat()) + prayerNames.get(testNum));
                Log.e(prayerTimes6.get(testNum) + " tom5 ", Integer.toString(loadDat()) + prayerNames.get(testNum));
                Log.e(prayerTimes7.get(testNum) + " tom6 ", Integer.toString(loadDat()) + prayerNames.get(testNum));*/
                //Log.e(prayerTimes.get(testNum) + "tom7 ", Integer.toString(loadDat()) + prayerNames.get(testNum));
                //Log.e(prayerTimes.get(5), Integer.toString(loadDat()) + prayerNames.get(5));
                //Log.e(prayerTimes.get(4), Integer.toString(loadDat()) + prayerNames.get(4));
                //Log.e(prayerTimes.get(3), Integer.toString(loadDat()) + prayerNames.get(3));
                //Log.e(prayerTimes.get(2), Integer.toString(loadDat()) + prayerNames.get(2));
                //Log.e(prayerTimes.get(1), Integer.toString(loadDat()) + prayerNames.get(1));
                //Log.e(prayerTimes.get(0), Integer.toString(loadDat()) + prayerNames.get(0));

                //Day after
                GregorianCalendar fajrCal3 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 2, Integer.parseInt(prayerTimes3.get(0).split(":")[0]), Integer.parseInt(prayerTimes3.get(0).split(":")[1]));
                GregorianCalendar dhurCal3 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 2, Integer.parseInt(prayerTimes3.get(2).split(":")[0]), Integer.parseInt(prayerTimes3.get(2).split(":")[1]));
                GregorianCalendar maghribCal3 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 2, Integer.parseInt(prayerTimes3.get(5).split(":")[0]), Integer.parseInt(prayerTimes3.get(5).split(":")[1]));

                //After that...
                GregorianCalendar fajrCal4 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 3, Integer.parseInt(prayerTimes4.get(0).split(":")[0]), Integer.parseInt(prayerTimes4.get(0).split(":")[1]));
                GregorianCalendar dhurCal4 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 3, Integer.parseInt(prayerTimes4.get(2).split(":")[0]), Integer.parseInt(prayerTimes4.get(2).split(":")[1]));
                GregorianCalendar maghribCal4 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 3, Integer.parseInt(prayerTimes4.get(5).split(":")[0]), Integer.parseInt(prayerTimes4.get(5).split(":")[1]));


                //Log.e("TEST1", String.valueOf(Integer.parseInt(prayerTimes.get(0).split(":")[0])));
                //Log.e("TEST2", String.valueOf(Integer.parseInt(prayerTimes.get(0).split(":")[1])));
                //Log.e("Alarm is SET", prayerTimes.get(5).split(":")[0] + ":" + prayerTimes.get(5).split(":")[1]);
                //Log.e("Fajr is SET", prayerTimes.get(0).split(":")[0] + ":" + prayerTimes.get(0).split(":")[1]);
                //Log.e("dhur is SET", prayerTimes.get(2).split(":")[0] + ":" + prayerTimes.get(2).split(":")[1]);
                long millis = myCal.getTimeInMillis();

/*
                //Log.e(loadLat(), loadLon());
                CharSequence a = Long.toString(myCal.getTimeInMillis()) + " " + Long.toString(System.currentTimeMillis());
                if(currentHour < Integer.parseInt(prayerTimes.get(2).split(":")[0])){
                    //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), pendingIntent3);
                    //Log.e(prayerTimes.get(2), Integer.toString(loadDat()) + prayerNames.get(2) + " SET");
                }*/
                //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), pendingIntent);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), pendingIntent2);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() - 5000, pendingIntent);

/*
                Toast.makeText(Main2Activity.this, String.valueOf(latitude) + " " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
*/


/*


                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), testPendingIntent1);
                Log.e("FajrTomSet:",String.valueOf(fajrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(fajrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal2.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(fajrCal2.get(Calendar.MINUTE)));
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), testPendingIntent1);
                Log.e("DhurTomSet:",String.valueOf(dhurCal2.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal2.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(dhurCal2.get(Calendar.MINUTE)));
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), testPendingIntent1);
                Log.e("MaghribTomSet:",String.valueOf(maghribCal2.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal2.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(maghribCal2.get(Calendar.MINUTE)));


                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal3.getTimeInMillis(), testPendingIntent1);
                Log.e("Fajr3Set:",String.valueOf(fajrCal3.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal3.get(Calendar.MONTH)+1) + "/" +String.valueOf(fajrCal3.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal3.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(fajrCal3.get(Calendar.MINUTE)));
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal3.getTimeInMillis(), testPendingIntent1);
                Log.e("Dhur3Set:",String.valueOf(dhurCal3.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal3.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal3.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal3.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal3.get(Calendar.MINUTE)));
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal3.getTimeInMillis(), testPendingIntent1);
                Log.e("Maghrib3Set:",String.valueOf(maghribCal3.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal3.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal3.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal3.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal3.get(Calendar.MINUTE)));

                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal4.getTimeInMillis(), testPendingIntent1);
                Log.e("Fajr4Set:",String.valueOf(fajrCal4.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal4.get(Calendar.MONTH)+1) + "/" +String.valueOf(fajrCal4.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal4.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal4.get(Calendar.MINUTE)));
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal4.getTimeInMillis(), testPendingIntent1);
                Log.e("Dhur4Set:",String.valueOf(dhurCal4.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal4.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal4.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal4.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal4.get(Calendar.MINUTE)));
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal4.getTimeInMillis(), testPendingIntent1);
                Log.e("Maghrib4Set:",String.valueOf(maghribCal4.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal4.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal4.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal4.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal4.get(Calendar.MINUTE)));
*/

                //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000 , pendingIntent);
                //alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60, pendingIntent);

                //PendingIntent alarmIntent = PendingIntent.getBroadcast(Main2Activity.this, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+25000, pendingIntent);

                saveAlarm("on");
                Toast.makeText(Main2Activity.this, "Adhans set for today", Toast.LENGTH_SHORT).show();
            }
        });


        //Alarm off onClick listener
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_manager.cancel(pendingIntent);
                alarm_manager.cancel(pendingIntent2);
                alarm_manager.cancel(pendingIntent3);
                alarm_manager.cancel(testPendingIntent1);
                alarm_manager.cancel(dynamicDhurPendingIntent);
                alarm_manager.cancel(dynamicFajrPendingIntent);
                alarm_manager.cancel(dynamicIshaPendingIntent);
                alarm_manager.cancel(dynamicAsrPendingIntent);
                alarm_manager.cancel(dynamicMaghribPendingIntent);

                saveAlarm("off");

                Toast.makeText(Main2Activity.this, "Adhans Off", Toast.LENGTH_SHORT).show();

                //i2.putExtra("extra", "alarm off");
                //sendBroadcast(i2);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public String loadLon(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        return (myMethod);
    }

    //load latitude data
    public String loadLat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
        return (myMethod);
    }

    //Save alarm state
    public void saveAlarm(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alarm", meth);
        editor.commit();
    }

    //Method to load alarm state
    public String loadAlarm(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("alarm", defaultMethod);
        return (myMethod);
    }

    public void save(int meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("method", Integer.toString(meth));
        editor.commit();
    }

    //Method to save daylight savings option
    public void saveDaylight(boolean meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("daylight", Boolean.toString(meth));
        editor.commit();
    }

    public void showmap(View v){
        //  function to show location in google maps
        Uri gmmIntentUri = Uri.parse("geo:"+loadDat1());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public int loadDat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("method", defaultMethod);
        return Integer.parseInt(myMethod);
    }

    public String loadDaylight(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("daylight", defaultMethod);
        return (myMethod);
    }

    public int loadNumericInstance() {
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt("numTimes", 0);
        return myMethod;
    }

    public String loadDat1(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        String myMethod1 = sharedPreferences.getString("lat", defaultMethod);
        // Returns latitude, longitude
        return (myMethod1 + "," + myMethod);
    }

    public void saveHomeLocation(String meth, String meth2){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("homeLat", (meth));
        editor.putString("homeLon", (meth2));
        Log.e("HOMELOCATION-->", meth+meth2);
        editor.commit();
    }

    public ArrayList<String> loadHomeLocation(){
        ArrayList<String> homeL = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
        homeL.add(sharedPreferences.getString("homeLat", defaultMethod));
        homeL.add(sharedPreferences.getString("homeLon", defaultMethod));
        return (homeL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(Main2Activity.this, "Already on settings", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}