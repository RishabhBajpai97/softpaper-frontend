package com.example.blue_beast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

public class MainActivity extends AppCompatActivity {

    private ContentValues contentValues = new ContentValues();
    private Uri MY_REDIRECT_URI = Uri.parse("in.softpaper.app://auth");
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
                        ResponseTypeValues.CODE, // the response_type value: we want a code
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
                        (resp1, ex1) -> {
                            if (resp1 != null) {
                                // exchange succeeded
                                storeTokenLocally(resp1);

                            }  // authorization failed, check ex for more details

                        });
            }
        }
    }

    private void storeTokenLocally(TokenResponse resp) {
        contentValues.put("token", resp.accessToken);

        @SuppressLint("Recycle")
        Cursor cursor = TokenProvider.getDbInstance().rawQuery(
                "SELECT * FROM " + TokenProvider.getDbTable(), null);

        if(cursor.getCount() == 0) {
            Uri uri = getContentResolver().insert(TokenProvider.CONTENT_URI,
                    contentValues);
            Toast.makeText(MainActivity.this, uri.toString(),
                    Toast.LENGTH_LONG).show();
        }
        else {
            int uri = getContentResolver().update(TokenProvider.CONTENT_URI,
                    contentValues, null, null);
            Toast.makeText(MainActivity.this, String.valueOf(uri),
                    Toast.LENGTH_LONG).show();
        }
    }
}