package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by hongjunjin on 7/22/15.
 */
public class locationActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, AdapterView.OnItemClickListener {

    protected static final String restURL = "https://api.flickr.com/services/rest/";
    protected static final String searchMethod = "flickr.photos.search";
    protected static final int numberOfThreads = 20;

    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    protected static String lon;
    protected static String lat;
    private String radius;
    private String sort;

    private Flickr_login flickr;
    private ListView lv;
    protected static List<RowItem> rowItems;
    protected static NodeList nodeList;
    ProgressDialog progress;
    protected static long threadId;
    private List<Thread> threadList;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Log.d("ADebugTag", "Value: " + "onCreat");


        flickr = new Flickr_login();

        buildGoogleApiClient();
        createLocationRequest();
        this.mGoogleApiClient.connect();

    }

    public void spinnerHelper(){

        Log.d("ADebugTag", "Value: " + "in Spinner");

        TextView radiusText = (TextView) findViewById(R.id.radiusText);
        TextView sortText = (TextView) findViewById(R.id.sortText);
        radiusText.setText("Radius");
        sortText.setText("Sort by");


        Spinner radius_spinner = (Spinner) findViewById(R.id.radius_spinner);
        ArrayAdapter<CharSequence> radius_adapter = ArrayAdapter.createFromResource(this,
                R.array.radius, android.R.layout.simple_spinner_item);
        radius_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radius_spinner.setAdapter(radius_adapter);

        Spinner sorting_spinner = (Spinner) findViewById(R.id.sorting_spinner);
        final ArrayAdapter<CharSequence> sorting_adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting, android.R.layout.simple_spinner_item);
        sorting_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorting_spinner.setAdapter(sorting_adapter);

        Log.d("ADebugTag", "Value: " + "done spinner");

        radius_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {

                String radius_selected = parent.getItemAtPosition(pos).toString();
                String radiusNum = radius_selected.substring(0, 1);
                setRadius(radiusNum);

                searchPhotos(getRadius(), getSort(), getLat(), getLon());
                //Log.d("ADebugTag", "in radius spinner: " + Thread.currentThread().getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // your code here

            }

        });

        sorting_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean firstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {

                String sorting_selected = parent.getItemAtPosition(pos).toString();
                setSort(sorting_selected);

                if (firstTime) {
                    firstTime = false;

                } else {
                    searchPhotos(getRadius(), getSort(), getLat(), getLon());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    protected void createLocationRequest() {
        Log.d("ADebugTag", "Value: " + "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("ADebugTag", "Value: " + "building google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ADebugTag", "Value: " + "onLocationChange");
        Location tempLocation = mCurrentLocation;
        //Log.d("ADebugTag", "onLcationChange: " + Thread.currentThread().getName());

        mCurrentLocation = location;
        String latitude = Double.toString(mCurrentLocation.getLatitude());
        String longitude = Double.toString(mCurrentLocation.getLongitude());
        setLat(latitude);
        setLon(longitude);

        if (tempLocation != null){
            searchPhotos(getRadius(), getSort(), getLat(), getLon());
        }else{
            spinnerHelper();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("ADebugTag", "Value: " + "onConnected");
        startLocationUpdates();

    }

    public void searchPhotos(final String radius, String sorting, final String lat, final String lon){

        rowItems = new ArrayList<RowItem>();
        threadList = new ArrayList<Thread>();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d("ADebugTag", "Value: " + "in t thread");

                System.out.println("t theadId: " + Thread.currentThread().getId());
                //threadId = Thread.currentThread().getId();

                String perPage = "50";

                StringBuffer searchBuffer = new StringBuffer(restURL);
                searchBuffer.append("?method=");
                searchBuffer.append(searchMethod);
                searchBuffer.append("&api_key=");
                searchBuffer.append(flickr.getFlickrKey());
                searchBuffer.append("&tags=architecture");
                searchBuffer.append("&radius_units=mi");
                searchBuffer.append("&radius=");
                searchBuffer.append(radius);
                searchBuffer.append("&lat=");
                searchBuffer.append(lat);
                searchBuffer.append("&lon=");
                searchBuffer.append(lon);
                searchBuffer.append("&media=photo&per_page=");
                searchBuffer.append(perPage);
                String searchURL = searchBuffer.toString();

                try {
                    URL url = new URL(searchURL);
                    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                    DocumentBuilder b = null;
                    try {
                        b = f.newDocumentBuilder();
                        try {
                            Document doc = b.parse(url.openStream());
                            doc.getDocumentElement ().normalize();
                            NodeList nodes = doc.getDocumentElement().getChildNodes();
                            Node node = nodes.item(1); //get photos
                            nodeList = node.getChildNodes(); //photo list

                            MyThread firstThread = new MyThread();
                            threadId = firstThread.getId();
                            firstThread.start();
                            threadList.add(firstThread);


                            for (int i = 0; i < numberOfThreads - 1; i++){
                                MyThread t = new MyThread();
                                t.start();
                                threadList.add(t);
                            }

                            for (int i = 0; i < threadList.size(); i++){
                                try {
                                    threadList.get(i).join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            progress.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showList(getSort());


                                }
                            });


                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //show list of pictures

            }

        });
        t.start();

    }

    public void showList(String sorting){


        Log.d("ADebugTag", "title: " + "in showList");

        //Log.d("ADebugTag", "showList: " + Thread.currentThread().getName());

        if (sorting == null || sorting.equals("Distance")){
            sortByDistance();
        }else{
            sortByFavorite();
        }

        lv = (ListView) findViewById(R.id.list);

        CustomList adapter = new CustomList(this,
                R.layout.listitem, rowItems);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

    }

    public void sortByDistance(){

        Collections.sort(rowItems, new Comparator<RowItem>() {
            @Override
            public int compare(RowItem p1, RowItem p2) {
                return Float.compare(p1.getDist(), p2.getDist()); // Ascending
            }

        });

    }

    public void sortByFavorite(){
        Collections.sort(rowItems, new Comparator<RowItem>() {
            @Override
            public int compare(RowItem p1, RowItem p2) {
                return Integer.compare(p1.getFavoriteNum(), p2.getFavoriteNum()); // Ascending
            }

        });
        Collections.reverse(rowItems);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("ADebugTag", "showList: " + "im in on click");
        RowItem item = rowItems.get(position);

    }

    public String getLat(){
        return lat;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public String getLon(){
        return lon;
    }

    public void setLon(String lon){
        this.lon = lon;
    }

    public void setRadius(String radius){
        this.radius = radius;
    }

    public String getRadius(){
        return radius;
    }

    public String getSort(){
        return sort;
    }

    public void setSort(String sort){
        this.sort = sort;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();

    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }


}
