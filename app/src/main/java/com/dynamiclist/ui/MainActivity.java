package com.dynamiclist.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dynamiclist.R;
import com.dynamiclist.utilities.Constants;
import com.dynamiclist.utilities.NetworkConnection;
import com.dynamiclist.adapter.FactsArrayAdapter;
import com.dynamiclist.api.FactsAPI;
import com.dynamiclist.model.Facts;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, Callback<Facts> {


    private RecyclerView recyclerView;
    @SuppressWarnings("CanBeFinal")
    private ProgressDialog progressBar = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("unused")
    private NetworkConnection networkConnectivity;

    //private List<Facts> factsList;
    //private List<Row> rowItems;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkConnectivity = new NetworkConnection(this);
        progressBar = new ProgressDialog(MainActivity.this);
        createViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(networkConnectivity.isConnectionAvailable()) {
            dismissProgressBar();
            progressBar.setMessage(Constants.LOADING);
            progressBar.show();
            progressBar.setCancelable(false);
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

        setupTitleBar();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        //Code removed due to Lint error of deprecated api
                                //getResources().getColor(R.color.colorAccent),
                                //getResources().getColor(R.color.colorPrimary),
                                //getResources().getColor(R.color.colorPrimaryDark));
                                swipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * Utility to dismiss the progress bar
     */
    private void dismissProgressBar() {
        if(progressBar.isShowing()){
            progressBar.dismiss();
        }
    }

    private void dismissSwipetoRefresh(){
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
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
        if (networkConnectivity.isConnectionAvailable()) {
            processRequest();
        } else {
            swipeRefreshLayout.setRefreshing(false);
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
        dismissSwipetoRefresh();
        FactsArrayAdapter myFactsArrayAdapter = new FactsArrayAdapter(MainActivity.this, response.body().getRows());
        //myFactsArrayAdapter.notifyDataSetChanged();
        if(response.body().getTitle() !=null && getActionBar()!= null) {
            getActionBar().setTitle(response.body().getTitle());
        }
        recyclerView.setAdapter(myFactsArrayAdapter);}

    /**
     * OnFailure dismiss the progress bar if showing
     * also dismiss the swipe to refresh progress bar if showing.
     * @param t Instance of Exception thrown to be handled.
     */
    @Override
    public void onFailure(Throwable t) {
        dismissProgressBar();
        dismissSwipetoRefresh();
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
