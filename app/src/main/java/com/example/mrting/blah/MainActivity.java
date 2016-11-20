package com.example.mrting.blah;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.lang.ref.WeakReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

import com.CustomApplication.CustomApplication;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;

import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;

import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;

import com.github.mikephil.charting.*;

public class MainActivity extends AppCompatActivity {
    static private TextView txtStatus, txtStatus2, txtStatus3, textNotification;
    static private BandClient client= null;
    static DBHandler db;
    static long steps;
    static int time=0;
    static ArrayList<String> list= new ArrayList<>();
    static ArrayList<String> list2= new ArrayList<>();
    static ArrayAdapter<String> adapter, adapter2;
    static Calendar rightNow= Calendar.getInstance();
    static int previous= rightNow.getTime().getDate();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db= new DBHandler(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Clearing Databases", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                db.deleteNotes();
                db.deleteDrugTable();
                db.deleteDataTable();
            }
        });

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /*@Override
        public void onCreate(Bundle savedInstanceState) {
            reference= new WeakReference<Activity>(getActivity());
            super.onCreate(savedInstanceState);
        }*/
        //Get connected to band
        private boolean getConnectedBandClient() throws InterruptedException, BandException {
            if (client == null) {
                //Find paired bands
                BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
                if (devices.length == 0) {
                    //display message to user
                    return false;
                }
                client = BandClientManager.getInstance().create(CustomApplication.getCustomAppContext(), devices[0]);
            } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
                return true;
            }

            return ConnectionState.CONNECTED == client.connect().await();
        }
        private class BandPedometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (getConnectedBandClient()) {
                        client.getSensorManager().registerPedometerEventListener(mPedometerEventListener);
                    }
                    else{
                        appendToUI("Band isn't connected. Please make sure bluetooth is on and the bnad is in range.\n");
                    }
                } catch (BandException e) {
                    String exceptionMessage="";
                    switch (e.getErrorType()) {
                        case UNSUPPORTED_SDK_VERSION_ERROR:
                            exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                            break;
                        case SERVICE_ERROR:
                            exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                            break;
                        default:
                            exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                            break;
                    }
                    appendToUI(exceptionMessage);

                } catch (Exception e) {
                    appendToUI(e.getMessage());
                }
                return null;
            }
        }
        private class BandGSRSubscriptionTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (getConnectedBandClient()) {
                        client.getSensorManager().registerGsrEventListener(mGSREventListener);
                    }
                    else{
                        appendToUI("Band isn't connected. Please make sure bluetooth is on and the bnad is in range.\n");
                    }
                } catch (BandException e) {
                    String exceptionMessage="";
                    switch (e.getErrorType()) {
                        case UNSUPPORTED_SDK_VERSION_ERROR:
                            exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                            break;
                        case SERVICE_ERROR:
                            exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                            break;
                        default:
                            exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                            break;
                    }
                    appendToUI(exceptionMessage);

                } catch (Exception e) {
                    appendToUI(e.getMessage());
                }
                return null;
            }
        }
        private class SkinTemperatureSubscriptionTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (getConnectedBandClient()) {
                        client.getSensorManager().registerSkinTemperatureEventListener(mSkinTemperatureEventListener);
                    }
                    else{
                        appendToUI("Band isn't connected. Please make sure bluetooth is on and the bnad is in range.\n");
                    }
                } catch (BandException e) {
                    String exceptionMessage="";
                    switch (e.getErrorType()) {
                        case UNSUPPORTED_SDK_VERSION_ERROR:
                            exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                            break;
                        case SERVICE_ERROR:
                            exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                            break;
                        default:
                            exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                            break;
                    }
                    appendToUI(exceptionMessage);

                } catch (Exception e) {
                    appendToUI(e.getMessage());
                }
                return null;
            }
        }
        private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (getConnectedBandClient()) {
                        if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                        } else {
                            appendToUI("You have not given this application consent to access heart rate data yet."
                                    + " Please press the Heart Rate Consent button.\n");
                        }
                    } else {
                        appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                    }
                } catch (BandException e) {
                    String exceptionMessage="";
                    switch (e.getErrorType()) {
                        case UNSUPPORTED_SDK_VERSION_ERROR:
                            exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                            break;
                        case SERVICE_ERROR:
                            exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                            break;
                        default:
                            exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                            break;
                    }
                    appendToUI(exceptionMessage);

                } catch (Exception e) {
                    appendToUI(e.getMessage());
                }
                return null;
            }
        }
        private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
            protected Void doInBackground(WeakReference<Activity>... params){
                try{
                    if(getConnectedBandClient()){
                        if(params[0].get()!=null){
                            client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                                @Override
                                public void userAccepted(boolean b){}
                            });
                        }
                    }
                    else{
                        appendToUI("Band not connected");
                    }
                }
                catch(Exception e)
                {}
                return null;
            }
        }
        private BandPedometerEventListener mPedometerEventListener= new BandPedometerEventListener() {
            @Override
            public void onBandPedometerChanged(BandPedometerEvent bandPedometerEvent) {
                try {
                    steps=bandPedometerEvent.getStepsToday();
                } catch (InvalidBandVersionException e) {
                    e.printStackTrace();
                }
            }
        };
        private BandSkinTemperatureEventListener mSkinTemperatureEventListener= new BandSkinTemperatureEventListener() {
            @Override
            public void onBandSkinTemperatureChanged(final BandSkinTemperatureEvent bandSkinTemperatureEvent) {
                if(bandSkinTemperatureEvent!=null){
                    Calendar rightNow= Calendar.getInstance();
                    String date= getDate(rightNow);
                    new BandPedometerSubscriptionTask().execute();
                    db.addData(new BandData(time, 0, bandSkinTemperatureEvent.getTemperature(), null, 0, date, steps));
                    time++;
                    skinTemp(String.format("Skin Temperature= %f Degrees\n", bandSkinTemperatureEvent.getTemperature()));
                }
            }
        };
        //Heart Rate
        private BandHeartRateEventListener mHeartRateEventListener= new BandHeartRateEventListener() {
            @Override
            public void onBandHeartRateChanged(final BandHeartRateEvent event) {
                if(event!=null){
                    Calendar rightNow= Calendar.getInstance();
                    String date= getDate(rightNow);
                    //message to cloud
                    new BandPedometerSubscriptionTask().execute();
                    db.addData(new BandData(time, event.getHeartRate(), 0, event.getQuality().toString(),0, date, steps));
                    time++;
                    appendToUI(String.format("Heart Rate= %d beats per minute\n" + "Quality=%s\n", event.getHeartRate(), event.getQuality()));
                }
            }
        };
        private BandGsrEventListener mGSREventListener= new BandGsrEventListener() {
            @Override
            public void onBandGsrChanged(final BandGsrEvent bandGsrEvent) {
                if(bandGsrEvent!=null){
                    Calendar rightNow= Calendar.getInstance();
                    String date=getDate(rightNow);
                    new BandPedometerSubscriptionTask().execute();
                    db.addData(new BandData(time, 0, 0, null, bandGsrEvent.getResistance(), date, steps));
                    GSRToUI(String.format("Your resistance is:%d", bandGsrEvent.getResistance()));
                }
            }
        };
        private String getDate(Calendar rightNow){
            String date;
            Date now= rightNow.getTime();
            date= (now.getMonth()+1)+ "/" +  now.getDate()+ "/" + (now.getYear()-100)+ " " + now.getHours()+":"+now.getMinutes()+":"+now.getSeconds();
            return date;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                Button btnConsent, btnStart, tempStart, btnGSR;
                ImageView logo;
                final WeakReference<Activity> reference= new WeakReference<Activity>(getActivity());
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);

                logo=(ImageView) rootView.findViewById(R.id.logo);
                logo.setImageResource(R.drawable.terrabio);

                btnConsent= (Button) rootView.findViewById(R.id.btnConsent);
                btnStart= (Button) rootView.findViewById(R.id.btnStart);
                /*btnGSR=(Button) rootView.findViewById(R.id.btnGSR);
                tempStart= (Button) rootView.findViewById(R.id.btnSkinTemp);*/

                txtStatus= (TextView) rootView.findViewById(R.id.txtStat);
                txtStatus2= (TextView) rootView.findViewById(R.id.txtStat2);
                txtStatus3= (TextView) rootView.findViewById(R.id.txtStat3);

                btnConsent.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onClick(View v) {
                        new HeartRateConsentTask().execute(reference);
                    }
                });
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar rightNow= Calendar.getInstance();
                        new HeartRateSubscriptionTask().execute();
                        new SkinTemperatureSubscriptionTask().execute();
                        new BandGSRSubscriptionTask().execute();
                        if(rightNow.getTime().getDate()==previous+1){
                            db.deleteDataTable();
                            db.deleteDrugTable();
                            db.deleteNotes();
                            previous=rightNow.getTime().getDate();
                        }
                    }
                });
                /*tempStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SkinTemperatureSubscriptionTask().execute();
                    }
                });
                btnGSR.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new BandGSRSubscriptionTask().execute();
                    }
                });*/

                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                ImageButton heartTable, skinTempTable, GSRTable;
                final ListView list;

                heartTable= (ImageButton) rootView.findViewById(R.id.produceTableHeart);
                skinTempTable=(ImageButton) rootView.findViewById(R.id.produceTableSkinTemp);
                GSRTable= (ImageButton) rootView.findViewById(R.id.produceTableGSR);

                list= (ListView) rootView.findViewById(R.id.listView);

                heartTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList array_list=db.getDataAsString(50, "heart");
                        final ArrayAdapter arrayAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, array_list);
                        list.setAdapter(arrayAdapter);
                    }
                });
                skinTempTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList array_list=db.getDataAsString(50, "skintemp");
                        final ArrayAdapter arrayAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, array_list);
                        list.setAdapter(arrayAdapter);
                    }
                });
                GSRTable.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        ArrayList array_list=db.getDataAsString(50, "gsr");
                        final ArrayAdapter arrayAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, array_list);
                        list.setAdapter(arrayAdapter);
                    }
                });

                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3){
                final View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
                final LineChart lineChart= (LineChart) rootView.findViewById(R.id.chart);
                textNotification= (TextView) rootView.findViewById(R.id.textNotification);
                Button heartGraph, skinTempGraph, GSRGraph;

                heartGraph= (Button) rootView.findViewById(R.id.produceGraphHeart);
                skinTempGraph=(Button) rootView.findViewById(R.id.produceGraphTemp);
                GSRGraph= (Button) rootView.findViewById(R.id.produceGraphGSR);
                lineChart.getAxisRight().setDrawLabels(false);
                lineChart.getAxisRight().setStartAtZero(false);
                lineChart.getAxisLeft().setStartAtZero(false);

                heartGraph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Entry> entries = new ArrayList<Entry>();
                        ArrayList<BandData> datas = new ArrayList<BandData>();
                        datas = db.getAllData(30, "heart");
                        int iterations = 30;
                        if(datas==null){
                            lineChart.setNoDataText("Not enough Data");
                            appendToTextNotification("Not enough Data");
                        }
                        else{
                            appendToTextNotification(" ");
                            for (int i = 0; i < iterations; i++) {
                                int heartRate = datas.get(i).getHeartRate();
                                //int timeID= datas.get(i).getTimeID();
                            /*if(datas.get(i).getQuality()=="ACQUIRING"){
                                lineChart.setDescriptionColor(3);
                            }*/ //change colour for the different data qualities???
                                entries.add(new Entry(heartRate, i));
                            }
                            LineDataSet dataset = new LineDataSet(entries, "Heart Rate");
                            ArrayList<String> timeID = new ArrayList<String>();
                            for (int i = iterations-1; i >= 0; i--) {
                                String time = datas.get(i).getTimeIDAsString();
                                timeID.add(time);
                            }
                            final LineData data = new LineData(timeID, dataset);
                            dataset.setDrawCubic(true);
                            dataset.setDrawValues(false);
                            dataset.setLineWidth(6);
                            lineChart.getAxisLeft().setAxisMinValue(60);
                            lineChart.getAxisLeft().setAxisMaxValue(120);
                            lineChart.setData(data);
                        }
                    }
                });
                skinTempGraph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Entry> entries = new ArrayList<Entry>();
                        ArrayList<BandData> datas = new ArrayList<BandData>();
                        datas = db.getAllData(30, "skintemp");
                        if(datas==null){
                            lineChart.setNoDataText("Not enough Data");
                            appendToTextNotification("Not enough Data");
                        }
                        else{
                            appendToTextNotification(" ");
                            int iterations = 30;
                            for (int i = 0; i < iterations; i++) {
                                float skinTemperature = datas.get(i).getSkinTemperature();
                                //int timeID= datas.get(i).getTimeID();
                            /*if(datas.get(i).getQuality()=="ACQUIRING"){
                                lineChart.setDescriptionColor(3);
                            }*/ //change colour for the different data qualities???
                                entries.add(new Entry(skinTemperature, i));
                            }
                            LineDataSet dataset = new LineDataSet(entries, "Skin Temperature");
                            ArrayList<String> timeID = new ArrayList<String>();
                            for (int i = iterations - 1; i >= 0; i--) {
                                String time = datas.get(i).getTimeIDAsString();
                                timeID.add(time);
                            }
                            LineData data = new LineData(timeID, dataset);
                            dataset.setDrawCubic(true);
                            dataset.setDrawValues(false);
                            lineChart.getAxisLeft().setAxisMinValue(20);
                            lineChart.getAxisLeft().setAxisMaxValue(40);
                            lineChart.setData(data);
                        }
                    }
                });
                GSRGraph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Entry> entries = new ArrayList<Entry>();
                        ArrayList<BandData> datas = new ArrayList<BandData>();
                        datas = db.getAllData(30, "gsr");
                        int iterations = 20;
                        if (datas == null) {
                            lineChart.setNoDataText("Not enough Data");
                            appendToTextNotification("Not enough Data");
                        } else {
                            appendToTextNotification(" ");
                            for (int i = 0; i < iterations; i++) {
                                int GSR = datas.get(i).getGSR();
                                //int timeID= datas.get(i).getTimeID();
                            /*if(datas.get(i).getQuality()=="ACQUIRING"){
                                lineChart.setDescriptionColor(3);
                            }*/ //change colour for the different data qualities???
                                entries.add(new Entry(GSR, i));
                            }
                            LineDataSet dataset = new LineDataSet(entries, "GSR");
                            ArrayList<String> timeID = new ArrayList<String>();
                            for (int i = iterations - 1; i >= 0; i--) {
                                String time = datas.get(i).getTimeIDAsString();
                                timeID.add(time);
                            }
                            LineData data = new LineData(timeID, dataset);
                            dataset.setDrawCubic(true);
                            dataset.setDrawValues(false);
                            //lineChart.getAxisLeft().setAxisMinValue(60);
                            //lineChart.getAxisLeft().setAxisMaxValue(120);
                            lineChart.setData(data);
                            lineChart.notifyDataSetChanged();

                        }
                    }
                });
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==4){
                final View rootView = inflater.inflate(R.layout.fragment_main4, container, false);
                adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
                adapter2= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list2);
                final String[] text = new String[1];
                final Spinner s1=(Spinner) rootView.findViewById(R.id.drugsSpinner);
                Button btnAdd=(Button) rootView.findViewById(R.id.btnAdd);
                Button add=(Button) rootView.findViewById(R.id.add);
                Button getData= (Button) rootView.findViewById(R.id.display);
                final EditText txtItem = (EditText) rootView.findViewById(R.id.txtItem);
                final EditText notes= (EditText) rootView.findViewById(R.id.textBox);
                final ListView lister= (ListView) rootView.findViewById(R.id.list314);

                getData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> string= new ArrayList<String>();
                        string= db.getAllDrugsAsString();//getDrug(0,"5/3/116 15:24:22").toDrugString();
                        final ArrayAdapter arrayAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, string);
                        lister.setAdapter(arrayAdapter);
                    }
                });
                notes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            text[0] = notes.getText().toString();
                            handled = true;
                        }
                        return handled;
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string= txtItem.getText().toString();
                        if(list.contains(string) || string== null){
                            txtItem.setText("");
                        }
                        else{
                            list.add(string);
                            txtItem.setText("");
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar rightNow= Calendar.getInstance();
                        String date= getDate(rightNow);
                        String sp1= String.valueOf(s1.getSelectedItem());
                        if(sp1==null){
                            sp1=txtItem.getText().toString();
                        }
                        text[0]= notes.getText().toString();
                        Drug drug= new Drug(date,sp1, text[0]);
                        db.addDrug(drug);
                    }
                });

                s1.setAdapter(adapter);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //s1.setOnItemSelectedListener(new YourItemSelectedListener);
                return rootView;
            }
            else{
                final View rootView = inflater.inflate(R.layout.fragment_main5, container, false);
                Button btnAdd= (Button) rootView.findViewById(R.id.display2);
                final EditText symptom= (EditText) rootView.findViewById(R.id.txtItem2);
                final ListView list= (ListView) rootView.findViewById(R.id.list3142);

                ArrayList<String> string = new ArrayList<String>();
                string = db.getAllNotesAsString();//getDrug(0,"5/3/116 15:24:22").toDrugString();
                final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, string);
                list.setAdapter(arrayAdapter);


                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String[] text = new String[1];
                        Calendar rightNow = Calendar.getInstance();
                        String date = getDate(rightNow);
                        text[0] = symptom.getText().toString();
                        Note note = new Note(date, text[0]);
                        db.addNote(note);
                        ArrayList<String> string = new ArrayList<String>();
                        string = db.getAllNotesAsString();//getDrug(0,"5/3/116 15:24:22").toDrugString();
                        final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, string);
                        list.setAdapter(arrayAdapter);

                    }
                });
                return rootView;
            }
        }
        /*public class YourItemSelectedListener implements AdapterView.OnItemSelectedListener{
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                String selected= parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected (AdapterView parent){}
        }*/
        private void appendToTextNotification(final String string) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textNotification.setText(string);
                }
            });
        }
        private void appendToUI(final String string) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtStatus.setText(string);
                }
            });
        }
        private void skinTemp(final String string){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtStatus2.setText(string);
                }
            });
        }
        private void GSRToUI(final String string){
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    txtStatus3.setText(string);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Stream";
                case 1:
                    return "Tables";
                case 2:
                    return "Graphs";
                case 3:
                    return "Drugs";
                case 4:
                    return "Log";
            }
            return null;
        }
    }
}
