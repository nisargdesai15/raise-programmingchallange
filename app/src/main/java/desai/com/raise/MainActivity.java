package desai.com.raise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<GiftCard> mGiftCards = new ArrayList<>();
    String url = "http://raise-interviews.herokuapp.com/offer";


    private static final String TAG_OFFERS = "offer";
    private static final String TAG_BRAND = "brand";
    private static final String TAG_VALUE = "value";
    private static final String TAG_COST = "cost";
    private static final String TAG_BONUS = "bonus";
    JSONArray offers = null;
    private Spinner mSpinner;
    private Button mSubmit;
    private String mBonusPoint;
    public static final String PointPreferance = "pointPref";
    SharedPreferences sharedpreferences;


    @Override
    protected void onResume() {
        super.onResume();

        //Registering Broadcast Receiver for Network State
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //UnRegistering Broadcast Receiver
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinner = (Spinner) findViewById(R.id.spinner2);
        mSubmit = (Button) findViewById(R.id.submit);

        mSubmit.setOnClickListener(mSubmitListener);

    }

    //listener for Submit survey button
    private View.OnClickListener mSubmitListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(isNetworkAvailable()) {
                UpdateRewardsPoint(100);

                new CompleteSurvey().execute(mSpinner.getSelectedItem().toString());
            } else {
                Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_LONG).show();
            }


        }
    };

    //jsonParser for GiftCard Details
    private ArrayList<GiftCard> getGiftCardDetails(String jsonData) throws JSONException {

        ArrayList<GiftCard> giftCards = new ArrayList<>();

        JSONObject jsonObj = new JSONObject(jsonData);

        // Getting JSON Array node
        offers = jsonObj.getJSONArray(TAG_OFFERS);

        // looping through All Offers

        for (int i = 0; i < offers.length(); i++) {
            JSONObject c = offers.getJSONObject(i);

            String brand = c.getString(TAG_BRAND);
            String value = c.getString(TAG_VALUE);
            String cost = c.getString(TAG_COST);

            GiftCard giftCard = new GiftCard();
            giftCard.setBrand(brand);
            giftCard.setValue(value);
            giftCard.setCost(cost);
            giftCards.add(giftCard);
        }
        return giftCards;
    }


    //Json Parser for Bonuspoint
    private String getBonusPoint(String jsonData) throws JSONException {
        String bonusPoint ="";
        JSONObject jsonObj = new JSONObject(jsonData);
        bonusPoint = jsonObj.getString(TAG_BONUS);
        return bonusPoint;
    }

    //getting brand Details for Survey
    private class GetBrands extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    mGiftCards = getGiftCardDetails(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //adapter for Spinner
            BrandNameAdapter brandNameAdapter = new BrandNameAdapter(getApplicationContext(), R.layout.spinner_text, mGiftCards);
            mSpinner.setAdapter(brandNameAdapter);
        }
    }


    //submitting Survey result to the Server
    private class CompleteSurvey extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... arg) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("brand", arg[0]));
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST, params);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    mBonusPoint = getBonusPoint(jsonStr);

                } catch (JSONException e) {

                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(mBonusPoint != null)
                 UpdateRewardsPoint(Integer.parseInt(mBonusPoint));
            else {
                Toast.makeText(getApplicationContext(),"Error in Response",Toast.LENGTH_LONG).show();
            }


        }
    }


    //check networkState
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //store reward point data
    public void UpdateRewardsPoint(int rewardPoint) {
        sharedpreferences = getSharedPreferences(PointPreferance, Context.MODE_PRIVATE);
        int rewardPoints = sharedpreferences.getInt("RewardsPoint", 0);
        rewardPoints = rewardPoints + rewardPoint;
        if(rewardPoints % 500 == 0){
            Toast.makeText(getApplicationContext(),"You Have Won " + String.valueOf(rewardPoints)+" reward Point",Toast.LENGTH_LONG).show();
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("RewardsPoint", rewardPoints);
        editor.commit();
    }

    //Adding Receiver to check Network State and call Web Service
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {

                new GetBrands().execute();
            }
            else{
                Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_LONG).show();

            }



        }
    };


}
