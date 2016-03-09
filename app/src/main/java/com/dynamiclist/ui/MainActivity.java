package com.dynamiclist.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.dynamiclist.R;
import com.dynamiclist.adapter.FactsArrayAdapter;
import com.dynamiclist.api.FactsAPI;
import com.dynamiclist.model.Facts;
import com.dynamiclist.Utilities.Constants;
import com.dynamiclist.Utilities.NetworkConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, Callback<Facts> {


    private ListView mListView;
    @SuppressWarnings("CanBeFinal")
    private ProgressDialog mProgressBar = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressWarnings("unused")
    private NetworkConnection mNetworkConnectivity;

    //private List<Facts> mFactsList;
    //private List<Row> mRowItems;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetworkConnectivity = new NetworkConnection(this);
        mProgressBar = new ProgressDialog(MainActivity.this);
        createViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNetworkConnectivity.isConnectionAvailable()) {
            if(mProgressBar.isShowing()){
                dismissProgressBar();
            }
            mProgressBar.setMessage(Constants.LOADING);
            mProgressBar.show();
            mProgressBar.setCancelable(false);
      //      createViews();
            processRequest();
        }else{
            Toast.makeText(MainActivity.this, Constants.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructs the view elements
     */
    private void createViews(){


        mListView = (ListView) findViewById(R.id.listView);

        setupTitleBar();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent),
                        ContextCompat.getColor(this,R.color.colorPrimary),
                        ContextCompat.getColor(this,R.color.colorPrimaryDark));
        //Code removed due to Lint error of deprecated api
                                //getResources().getColor(R.color.colorAccent),
                                //getResources().getColor(R.color.colorPrimary),
                                //getResources().getColor(R.color.colorPrimaryDark));
                                mSwipeRefreshLayout.setOnRefreshListener(this);
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
    private void processRequest(){
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
        dismissProgressBar();
        if (mNetworkConnectivity.isConnectionAvailable()) {
            processRequest();
        } else {
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
     * @param response response JSON received from Network Calls
     * @param retrofit instance of Retrofit passed by default
     */
    @Override
    public void onResponse(Response<Facts> response, Retrofit retrofit) {
        dismissProgressBar();
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        FactsArrayAdapter myFactsArrayAdapter = new FactsArrayAdapter(MainActivity.this, response.body().getRows());
        if(response.body().getTitle() !=null && getActionBar()!= null) {
            getActionBar().setTitle(response.body().getTitle());
        }
        mListView.setAdapter(myFactsArrayAdapter);
    }

    /**
     * OnFailure dismiss the progress bar if showing
     * also dismiss the swipe to refresh progress bar if showing.
     * @param t Instance of Exception thrown to be handled.
     */
    @Override
    public void onFailure(Throwable t) {
        dismissProgressBar();
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        Toast.makeText(this, "Failed to establish connection"+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Failed to create connection" + t.getLocalizedMessage());
    }

    /**
     * Small Utility to change the Actionbar & status bar color
     */
    private void setupTitleBar(){
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.actionBarColor));
        }
    }
}
