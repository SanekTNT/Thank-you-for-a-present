package com.example.presentator.modules.friends;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.presentator.model.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FriendsService {

    private FriendsAdapter friendsAdapter;
    private DatabaseReference databaseReference;
    private Map<User, Boolean> userFriendStatusMap;
    private Map<User, String> userUIDs;

    public FriendsService(FriendsAdapter friendsAdapter) {
        this.friendsAdapter = friendsAdapter;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.userFriendStatusMap = new HashMap<>();
        this.userUIDs = new HashMap<>();
        friendsAdapter.setFriendsService(this);
    }

    public void getFriendsByUser(FirebaseUser user) {
        friendsAdapter.clearItems();
        databaseReference.child("friends").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String friendUid = d.getValue(String.class);
                    getFriendFromUIDAndPutItInUserList(friendUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendsActivity",
                        "startObserveFriendsEvents: database error occurred",
                        databaseError.toException());
            }
        });
    }

    private void getFriendFromUIDAndPutItInUserList(String uid) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("users_new").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!firebaseUser.getUid().equals(uid)) {
                    userUIDs.put(user, uid);
                    userFriendStatusMap.put(user, Boolean.TRUE);
                    friendsAdapter.addItem(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendsActivity",
                        "startObserveFriendsEvents: database error occurred",
                        databaseError.toException());
            }
        });
    }

    public void getUsersByNickname(String nickname) {
        friendsAdapter.clearItems();
        databaseReference.child("users_new").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    User user = d.getValue(User.class);
                    if (user.getNick().contains(nickname)) {
                        userUIDs.put(user, d.getKey());
                        friendsAdapter.addItem(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendsActivity",
                        "startObserveFriendsEvents: database error occurred",
                        databaseError.toException());
            }
        });
    }

    public boolean isUserFriendOfCurrentFirebaseUser(User user){
        if ((userFriendStatusMap.containsKey(user))
                && (userFriendStatusMap.get(user).equals(Boolean.TRUE))) {
            return true;
        }
        return false;
    }

    public void putFriendUIDIntoFirebaseDatabase(FirebaseUser firebaseUser, User user){
        FirebaseDatabase.getInstance().getReference()
                .child("friends")
                .child(firebaseUser.getUid()).push().setValue(userUIDs.get(user));
        userFriendStatusMap.put(user, true);
    }
}
