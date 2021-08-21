package com.example.blue_beast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private ContentValues contentValues = new ContentValues();
    private Uri MY_REDIRECT_URI = Uri.parse("https://com.android.chrome");
    private String MY_CLIENT_ID = "foo";
    private int RC_AUTH = 100;
    private AuthorizationService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthorizationServiceConfiguration serviceConfig =
                new AuthorizationServiceConfiguration(
                        Uri.parse("https://pacific-island-88998.herokuapp.com/auth"),
                        Uri.parse("https://pacific-island-88998.herokuapp.com/token")
                );

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        MY_CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.ID_TOKEN, // the response_type value: we want a code
                        MY_REDIRECT_URI); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder
                .setScope("openid")
                .build();

        AuthorizationService authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        startActivityIfNeeded(authIntent, RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_AUTH) {
            assert data != null;
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            // ... process the response or exception ...

            mAuthService = new AuthorizationService(this);

            if(resp!=null) {
                mAuthService.performTokenRequest(
                        resp.createTokenExchangeRequest(),
                        new AuthorizationService.TokenResponseCallback() {
                            @Override public void onTokenRequestCompleted(
                                    TokenResponse resp, AuthorizationException ex) {
                                if (resp != null) {
                                    // exchange succeeded
                                    Log.e("TAG", "onTokenRequestCompleted: " + resp.accessToken );
                                } else {
                                    // authorization failed, check ex for more details
                                }
                            }
                        });
            }
            else {
                Log.e("TAG", "onActivityResult: " + "null" );
            }

        } else {
            // ...
        }
    }
}