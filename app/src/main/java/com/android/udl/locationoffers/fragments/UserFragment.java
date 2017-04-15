package com.android.udl.locationoffers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.ItemClick;
import com.android.udl.locationoffers.services.NotificationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private List<Message> messages;

    private String db_mode;

    private static final int MENU_START_SERVICE = 10;
    private static final int MENU_STOP_SERVICE = 20;

    public static UserFragment newInstance(String string) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString("db", string);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        db_mode = getArguments().getString("db");

        if (messages == null) messages = new ArrayList<>();
        MyAdapter adapter = new MyAdapter(messages, new ItemClick(getActivity(), mRecyclerView));
        mRecyclerView.setAdapter(adapter);
        if (messages.size() == 0) read();

        /* Swipe down to refresh */
        final SwipeRefreshLayout sr = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                read();
                sr.setRefreshing(false);
            }
        });
        sr.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);
        /* /Swipe down to refresh */


    }

    private void read () {

        String database = db_mode.equals("messages") ? "User messages" : "User removed";

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref =
                    db.getReference(database).child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter = (MyAdapter) mRecyclerView.getAdapter();
                    adapter.removeAll();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d("USER","snapshot: " +
                                postSnapshot.getValue(Message.class).getTitle());
                        Message message = postSnapshot.getValue(Message.class);

                        downloadImage(message);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void downloadImage (final Message message) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference =
                storage.getReferenceFromUrl("gs://location-offers.appspot.com");
        StorageReference imageReference =
                storageReference.child("user_images/"+message.getCommerce_uid()+".png");
        imageReference.getBytes(1024*1024).addOnSuccessListener(
                new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        message.setImage(BitmapUtils.byteArrayToBitmap(bytes));
                        adapter.add(message);
                    }
                });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        menu.add(Menu.NONE,MENU_START_SERVICE,Menu.NONE, "Start Message Detection");
        menu.add(Menu.NONE,MENU_STOP_SERVICE,Menu.NONE, "Stop Message Detection");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serviceIntent;

        switch (item.getItemId()) {
            case MENU_START_SERVICE:
                serviceIntent = new Intent(getActivity(), NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                getActivity().startService(serviceIntent);
                break;

            case MENU_STOP_SERVICE:
                serviceIntent = new Intent(getActivity(), NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                getActivity().stopService(serviceIntent);
                break;

        }
        return true;
    }
}
