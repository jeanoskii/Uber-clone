package com.upou.jeano.uber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upou.jeano.uber.HistoryRecyclerView.HistoryAdapter;
import com.upou.jeano.uber.HistoryRecyclerView.HistoryObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;
    private String customerOrDriver, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);

        mHistoryLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(), HistoryActivity.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);

        customerOrDriver = getIntent().getExtras().getString("customerOrDriver");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserHistoryIds();
    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOrDriver).child(userId).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot history : dataSnapshot.getChildren()) {
                        fetchRideInformation(history.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchRideInformation(String rideKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("History").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String rideId = dataSnapshot.getKey();
                    HistoryObject obj = new HistoryObject(rideId);
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList resultsHistory = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }
}
