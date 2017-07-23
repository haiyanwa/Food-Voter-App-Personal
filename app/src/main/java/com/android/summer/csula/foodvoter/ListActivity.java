package com.android.summer.csula.foodvoter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ListActivity extends AppCompatActivity implements RVoteAdapter.ListItemClickListener {

    private RVoteAdapter rVoteAdapter;
    private RecyclerView rVoteRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rVoteRecyclerView = (RecyclerView) findViewById(R.id.rv_vote_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rVoteRecyclerView.setLayoutManager(layoutManager);

        rVoteRecyclerView.setHasFixedSize(true);

        rVoteAdapter = new RVoteAdapter(Restaurant.generateRestarantList(), this);
        rVoteRecyclerView.setAdapter(rVoteAdapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
