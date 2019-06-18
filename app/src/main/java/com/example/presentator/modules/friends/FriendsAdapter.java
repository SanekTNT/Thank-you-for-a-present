package com.example.presentator.modules.friends;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.presentator.R;
import com.example.presentator.model.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private FriendsService friendsService;

    public void addItem(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }

    public void clearItems() {
        userList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    public FriendsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_user_item, viewGroup, false);
        return new FriendsAdapter.UserViewHolder(view);
    }

    public void onBindViewHolder(@NonNull FriendsAdapter.UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    public int getItemCount() {
        return userList.size();
    }

    public void setFriendsService(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImageView;
        private TextView nickTextView;
        private TextView nameTextView;
        private TextView genderTextView;
        private ImageButton friendStatus;

        public UserViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.profile_image_view);
            nickTextView = itemView.findViewById(R.id.nick_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            genderTextView = itemView.findViewById(R.id.gender_text_view);
            friendStatus = itemView.findViewById(R.id.friend_status_image_button);
        }

        public void bind(User user) {
            nickTextView.setText(user.getNick());
            nameTextView.setText(user.getName());
            genderTextView.setText(user.getGender().toString());
            if (friendsService.isUserFriendOfCurrentFirebaseUser(user)) {
                friendStatus.setImageResource(R.drawable.tick_button);
            } else {
                friendStatus.setImageResource(R.drawable.add_friend);
                friendStatus.setOnClickListener(view -> {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    friendsService.putFriendUIDIntoFirebaseDatabase(firebaseUser, user);
                    friendStatus.setImageResource(R.drawable.tick_button);
                });
            }
            Picasso.with(itemView.getContext()).load(user.getImageURL()).into(userImageView);
        }
    }
}
