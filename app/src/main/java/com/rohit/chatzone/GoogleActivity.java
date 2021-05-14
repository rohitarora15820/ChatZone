package com.rohit.chatzone;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class GoogleActivity extends AppCompatActivity {
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="GoogleActivity";
    private FirebaseAuth Auth;
    private Button SignOutButton;
    private int RC_SignIN=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        signInButton=findViewById(R.id.signIn);
        Auth=FirebaseAuth.getInstance();
        SignOutButton=findViewById(R.id.signOut);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        SignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                Toast.makeText(GoogleActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
                SignOutButton.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void signIn(){
        Intent SignIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(SignIntent,RC_SignIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SignIN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try{
            GoogleSignInAccount account=completeTask.getResult(ApiException.class);
            Toast.makeText(GoogleActivity.this,"Sign In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
            Intent intent=new Intent(GoogleActivity.this,ChatActivity.class);
            startActivity(intent);
        }catch (ApiException e){
            Toast.makeText(GoogleActivity.this,"Sign In UnSuccessfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }
    private void FirebaseGoogleAuth(GoogleSignInAccount account){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        Auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(GoogleActivity.this,"Successfully",Toast.LENGTH_SHORT).show();
                    FirebaseUser user=Auth.getCurrentUser();
                    updateUI(user);
            }else{
                    Toast.makeText(GoogleActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
        }
        });

    }
    private void updateUI(FirebaseUser firebaseUser){
        SignOutButton.setVisibility(View.VISIBLE);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){
            String personName=account.getDisplayName();
            String personGiveName=account.getGivenName();
            String personFamilyName=account.getFamilyName();
            String personEmail=account.getEmail();
            String personId=account.getId();
            Uri personPhoto=account.getPhotoUrl();
            Toast.makeText(GoogleActivity.this,personEmail+personPhoto,Toast.LENGTH_SHORT).show();
        }


    }
}
