package com.example.deepanshuaggarwal.receiptvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static int LOGIN_STATUS=0;
    public static String Useremail="";
    SignInButton sgnbtn;
    private static int CheckerOffline=0;
    private String xml="act";
    @Override
    protected void onStart() {
        super.onStart();
       SharedPreferences sharedPreferences=getSharedPreferences("LOGIN_STATUS",MODE_PRIVATE);
       String Text=sharedPreferences.getString("STATUS",null);
       if(Text!=null){
           CheckerOffline=1;
       }
       else{
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            LOGIN_STATUS=1;
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    String providerId = profile.getProviderId();
                    Useremail=profile.getEmail();
                    xml = profile.getDisplayName();
                }
                ;
            }
        }
        else {
            LOGIN_STATUS=0;
        }}
    }
    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                SharedPreferences.Editor prefs=getSharedPreferences("LOGIN_STATUS",MODE_PRIVATE).edit();
                prefs.putString("STATUS","True");
                prefs.apply();
                Toast.makeText(MainActivity.this,"Sign In Success",Toast.LENGTH_SHORT).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String name="Guest";
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
                        String providerId = profile.getProviderId();
                        name = profile.getDisplayName();
                        }
                    ;
                }
                Intent i=new Intent(MainActivity.this,MainInterface.class);
                i.putExtra("Name",name);
                MainActivity.this.startActivity(i);

            } else {
                Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(xml.equals("act") && CheckerOffline==0){;}
        else{*/Intent i=new Intent(MainActivity.this,MainInterface.class);
       // i.putExtra("Name",xml);
       //Bypassing Login for checking
        MainActivity.this.startActivity(i);/*}*/
        setContentView(R.layout.activity_main);
        sgnbtn=findViewById(R.id.googlesignin);
        sgnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        });




    }
}

/*Storytime
*IMP*==Image firebase m stored h but data ko realtime database m store karna h
//Authentication to storage required
//1 ka koi aur tareeka dhundo jisse ki RealtimeDatabase m store na karna pade(Maybe create a text file);
//-->Yaha se shuru-->View banana h dekhne k liye
usme uploaded and Unuploaded ko alag dikhana h
//FIle selector ko abhi k liye chod rhe h bahut changes karne padenge
//Data retrieve karne k liye alag option hoga
app offline data hi dikhaega view m
Data restore karenge na ki realtime m download karenge
 */