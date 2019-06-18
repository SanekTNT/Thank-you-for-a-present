package com.example.presentator.modules.friends;

import com.google.firebase.auth.FirebaseAuth;

public class FriendsController {

    private FriendsActivity friendsActivity;
    private FriendsService friendsService;
    private FriendsAdapter friendsAdapter;

    public FriendsController(FriendsActivity friendsActivity, FriendsAdapter friendsAdapter) {
        this.friendsActivity = friendsActivity;
        this.friendsAdapter = friendsAdapter;
        this.friendsService = new FriendsService(friendsAdapter);
    }

    public void displayUserFriends() {
        friendsService.getFriendsByUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    public void displayAllUsersByNickname(String nickname) {
        friendsService.getUsersByNickname(nickname);
    }
}
