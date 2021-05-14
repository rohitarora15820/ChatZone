package com.rohit.chatzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohit.chatzone.MessageActivity;
import com.rohit.chatzone.R;
import com.rohit.chatzone.model.Chat;
import com.rohit.chatzone.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
     private List<User> mUser;
    private boolean ischat;
    String theLastMessage;

    public UserAdapter(Context context,List<User> mUser,boolean ischat){
        this.context=context;
        this.mUser=mUser;
        this.ischat=ischat;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(context).inflate(R.layout.users_item,parent,false);
       return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

final User user=mUser.get(position);
holder.username.setText(user.getUsername());

if(user.getImageURL().equals("default")){
    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
}
else{
    Glide.with(context).load(user.getImageURL()).into(holder.profile_image);

}

if (ischat){
    lastmessage(user.getId(),holder.last_msg);
}else{
    holder.last_msg.setVisibility(View.GONE);
}
if(ischat){
    if (user.getStatus().equals("online")){
        holder.img_online.setVisibility(View.VISIBLE);
        holder.img_offline.setVisibility(View.GONE);
    }else{
        holder.img_online.setVisibility(View.GONE);
        holder.img_offline.setVisibility(View.VISIBLE);
    }
}else{
    holder.img_online.setVisibility(View.GONE);
    holder.img_offline.setVisibility(View.GONE);
}
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context, MessageActivity.class);
        intent.putExtra("userid",user.getId());
        context.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
         return  mUser.size();    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView img_online;
        private ImageView img_offline;
        private TextView last_msg;

        public ViewHolder(View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
        img_offline=itemView.findViewById(R.id.img_offline);
        img_online=itemView.findViewById(R.id.img_online);
        last_msg=itemView.findViewById(R.id.last_msg);

        }
    }
    private void lastmessage(final String userid, final TextView lasr_msg){
        theLastMessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             try{   for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||  (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))){
                        theLastMessage=chat.getMessage();


                    }
                }}catch (Exception e){}
                switch (theLastMessage){
                    case "default":
                        lasr_msg.setText("No Msg");
                        break;
                        default:
                            lasr_msg.setText(theLastMessage);
                            break;
                }
                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
