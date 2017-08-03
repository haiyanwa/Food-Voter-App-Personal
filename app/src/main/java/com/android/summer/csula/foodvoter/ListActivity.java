package com.android.summer.csula.foodvoter;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Yelp>, RVoteAdapter.ListItemClickListener, RVoteAdapter.SwitchListener {

    private RVoteAdapter rVoteAdapter;
    private RecyclerView rVoteRecyclerView;
    private Toast mToast;
    private final static String TAG = "ListActivity";

    private static final int ATASK_LOADER_ID = 0;

    //for testing
    private String latitude = "34.066530";
    private String longtitude = "-118.166560";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rVoteRecyclerView = (RecyclerView) findViewById(R.id.rv_vote_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rVoteRecyclerView.setLayoutManager(layoutManager);

        rVoteRecyclerView.setHasFixedSize(true);

        try{
            //rVoteAdapter = new RVoteAdapter(restaurant.generateRestarantList(), this, this);
            rVoteAdapter = new RVoteAdapter(this,DataRetriever.getRestaurants(latitude,longtitude).getBusinesses(),this,this);

        }catch(Exception e){

            rVoteAdapter = new RVoteAdapter(this,null,this,this);
            e.printStackTrace();
            Log.d(TAG, "Error: Failed to get data for rVoteAdapter");
        }
        rVoteRecyclerView.setAdapter(rVoteAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(ATASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Yelp> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Yelp>(this) {

            Yelp mCacheData = null;

            @Override
            protected void onStartLoading() {
                if (mCacheData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mCacheData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Yelp loadInBackground() {
                try{
                    //sync data -- pull data from api in json form then insert into database
                    Yelp yelp = DataRetriever.getRestaurants(latitude,longtitude);

                    //get cursor by querying database
                    return yelp;

                }catch (Exception e){
                    Log.d(TAG, "Failed to load data in the backgroud");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Yelp yelp) {
                mCacheData = yelp;
                super.deliverResult(yelp);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Yelp> loader, Yelp data) {
        rVoteAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<Yelp> loader) {
        rVoteAdapter.swapData(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if(mToast != null){
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + "clicked. ";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onSwitchSwiped(int swipedItemIndex, boolean swiped) {
        if(mToast != null){
            mToast.cancel();
        }
        String toastMessage = "";
        if(swiped){
            toastMessage = "Item #" + swipedItemIndex + " swipted on. ";
        }else{
            toastMessage = "Item #" + swipedItemIndex + " swipted off. ";
        }

        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
    }


}
