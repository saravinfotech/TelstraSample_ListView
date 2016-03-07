package com.dynamiclist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.dynamiclist.Utilities.Constants;
import com.dynamiclist.Utilities.NetworkConnection;
import com.dynamiclist.api.FactsAPI;
import com.dynamiclist.dataAdapter.FactsArrayAdapter;
import com.dynamiclist.pojo.Facts;
import com.dynamiclist.pojo.Row;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, Callback<Facts> {


    private ListView listView;
    private ProgressDialog mProgressBar = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    NetworkConnection networkConnectivity;

    private List<Facts> factsList;
    private List<Row>  rowItems;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkConnectivity = new NetworkConnection(this);
        listView = (ListView)findViewById(R.id.listView);

        if(networkConnectivity.isConnectionAvailable()) {
            mProgressBar = new ProgressDialog(MainActivity.this);
            mProgressBar.setMessage(Constants.LOADING);
            mProgressBar.show();
            mProgressBar.setCancelable(false);
            createViews();
            processRequest();
        }else{
            Toast.makeText(MainActivity.this, Constants.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Constructs the view elements
     */
    public void createViews(){

        listView = (ListView) findViewById(R.id.listView);

        setupTitleBar();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * Utility to dismiss the progress bar
     */
    private void dismissProgressBar() {
        if(mProgressBar.isShowing()){
            mProgressBar.dismiss();
        }
    }

    /**
     * This is the retrofit library initialization piece where the network call is made
     * return value is always a converted to type Facts POJO here
     */
    public void processRequest(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FactsAPI factsAPI = retrofit.create(FactsAPI.class);
        Call<Facts> call = factsAPI.getFacts();

        //Asynchronous call in retrofit library
        call.enqueue(this);
    }

    /**
     * Default call back method of Swipe to Refresh utility.
     * When this call back is initiated, check for network connectivity and
     * proceed with the request as applicable.
     */
    @Override
    public void onRefresh() {
        if (networkConnectivity.isConnectionAvailable()) {
            processRequest();
            dismissProgressBar();
        } else {
            dismissProgressBar();
            Toast.makeText(MainActivity.this, Constants.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Retrofit Callback methods
     * onSuccess dismiss the progress bar & swipe to refresh progress bar
     * if still active.
     *
     * Get the response & pass the JSON to Facts POJO class which in turn
     * assigns the JSON Objects to the Row POJO class for further processing.
     * @param response
     * @param retrofit
     */
    @Override
    public void onResponse(Response<Facts> response, Retrofit retrofit) {
        dismissProgressBar();
        Log.d(TAG, "Response JSON is"+response.body().getRows().toString());
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        FactsArrayAdapter myFactsArrayAdapter = new FactsArrayAdapter(MainActivity.this, response.body().getRows());
        Log.i(TAG, "Title value is " + response.body().getTitle());
        getActionBar().setTitle(response.body().getTitle());
        listView.setAdapter(myFactsArrayAdapter);
    }

    /**
     * OnFailure dismiss the progress bar if showing
     * also dismiss the swipe to refress progress bar if showing.
     * @param t
     */
    @Override
    public void onFailure(Throwable t) {
        dismissProgressBar();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        Toast.makeText(this, "Failed to establish connection"+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Failed to create connection" + t.getLocalizedMessage());
    }

    /**
     * Small Utility to change the Actionbar & status bar color
     */
    public void setupTitleBar(){
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.actionBarColor));
    }
}
