package com.android.summer.csula.foodvoter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity implements RVoteAdapter.ListItemClickListener, RVoteAdapter.SwitchListener {

    private RVoteAdapter rVoteAdapter;
    private RecyclerView rVoteRecyclerView;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rVoteRecyclerView = (RecyclerView) findViewById(R.id.rv_vote_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rVoteRecyclerView.setLayoutManager(layoutManager);

        rVoteRecyclerView.setHasFixedSize(true);

        rVoteAdapter = new RVoteAdapter(Restaurant.generateRestarantList(), this, this);
        rVoteRecyclerView.setAdapter(rVoteAdapter);
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
