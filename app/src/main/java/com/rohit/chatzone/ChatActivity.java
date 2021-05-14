package com.rohit.chatzone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohit.chatzone.model.User;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
   // private Button SignOutButton;
    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private GoogleSignInClient mGoogleSignInClient;

   /* String[] items={"Person1","Person2","Person3","Person4","Person5","Person6"};*/

    ViewPager viewPager;
    TabLayout tabLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatZone");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    profile_image=findViewById(R.id.profile_image);
    username=findViewById(R.id.username);
    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user=dataSnapshot.getValue(User.class);
            username.setText(user.getUsername());
            if(user.getImageURL().equals("default")){
            profile_image.setImageResource(R.mipmap.ic_launcher);}
            else{
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
            }
        }



        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    tabLayout=findViewById(R.id.tab);

    viewPager=findViewById(R.id.view_pager);
    ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.addFragment(new ChatFragment(),"Chats");
        viewPagerAdapter.addFragment(new UserFragment(),"User");
        viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

    viewPager.setAdapter(viewPagerAdapter);
  tabLayout.setupWithViewPager(viewPager);

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);


     /*   ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txt.setText(items[position]);
            }
        });*/




}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drawer,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id=item.getItemId();


         if(id==R.id.share){

            Intent i=new Intent(Intent.ACTION_SEND);
           i.setType("text/plain");

            i.putExtra(Intent.EXTRA_SUBJECT,"CHAT ZONE");
            String shareMessage="https://www.mediafire.com/file/565zys29963ch14/ChatZone.apk/file";
            i.putExtra(Intent.EXTRA_TEXT,shareMessage);

            startActivity(Intent.createChooser(i,"ShareVia"));
            return  true;
        }

        else         if(id==R.id.About){
            Intent i=new Intent(ChatActivity.this,AboutActivity.class);
            startActivity(i);
            return  true;
        }

        else         if(id==R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatActivity.this,RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();



            return  true;
        }
        return false;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void status(String status){
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);

    }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}



