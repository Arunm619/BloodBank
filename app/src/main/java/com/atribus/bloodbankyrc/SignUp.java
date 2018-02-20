package com.atribus.bloodbankyrc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private SignInButton signInButton;
    private LinearLayout lt_signup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Snackbar snackbar;
    private ProgressDialog dialog;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient googleApiClient;
    TextView tv_termsandconditions;

    public static final String DISPLAY_ONCE_PREFS = "display_only_once_spfile";

    public static final String DISPLAY_ONCE_KEY = "display_only_once_spkey";

    CheckBox cb_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        signInButton = findViewById(R.id.btn_signin);
        lt_signup = findViewById(R.id.lt_signin);
        cb_agree = findViewById(R.id.cb_agree);
        tv_termsandconditions = findViewById(R.id.tv_termsandconditions);
        dialog = new ProgressDialog(SignUp.this);
        // dialog.setTitle("Google Sign-In");
        dialog.setMessage("Validating Credentials...");


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                if (firebaseAuth.getCurrentUser() != null) {
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = null;
                    if (manager != null) {
                        info = manager.getActiveNetworkInfo();
                    }

                    if (info == null) {
                        Intent intent = new Intent(SignUp.this, NoInternet.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return;

                    } else {
                        //  Toast.makeText(getApplicationContext(), "Stable Internet is Available.", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(SignUp.this, UserActivity.class));
                    finish();
                }
            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        dialog.dismiss();
                        snackbar = Snackbar
                                .make(lt_signup, "Error occured.", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {
                //  Toast.makeText(SignUp.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                boolean a = isLocationEnabled(SignUp.this);

                //  Toast.makeText(SignUp.this, "Locaiton enabled? "+a , Toast.LENGTH_SHORT).show();
                if (!a) {
                    turngpson();

                }
            }

            @Override
            public void onPermissionDenied(ArrayList <String> deniedPermissions) {
                finish();
            }


        };
        //call permissions
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")

                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

                .check();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cb_agree.isChecked()) {
                    signIn();
                    dialog.show();

                } else {
                    Snackbar.make(lt_signup, "Agree to Terms And Conditions", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        tv_termsandconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLongDisclaimerwith();
            }
        });


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SignUp.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            // Toast.makeText(this, ""+personEmail, Toast.LENGTH_SHORT).show();
        }


        if (introductionCompletedPreviously()) {
            return;
        } else {
            final SharedPreferences sp = getSharedPreferences(DISPLAY_ONCE_PREFS, MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(DISPLAY_ONCE_KEY, true);
            startActivity(new Intent(this, IntroSlider.class));
            finish();
        }
    }

    private void callLongDisclaimerwith() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.TermsandPolicies);
        alertDialog.setMessage(R.string.TermsandPoliciesLongtext);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.setNegativeButton("See More", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                taketoterms();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();


    }

    private void taketoterms() {
        String url = "http://www.blooddonation.com/termsandpolicies";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void turngpson() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(SignUp.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult <LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback <LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SignUp.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }


    }


    private boolean introductionCompletedPreviously() {
        SharedPreferences sp = getSharedPreferences(DISPLAY_ONCE_PREFS, MODE_PRIVATE);
        return sp.getBoolean(DISPLAY_ONCE_KEY, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
     /*   ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }

        if (info == null) {
            Intent intent = new Intent(SignUp.this, NoInternet.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
return;

        }
        else
        {
            Toast.makeText(this, "Stable Internet is Available.", Toast.LENGTH_SHORT).show();
        }

*/
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        dialog.show();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            dialog.dismiss();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss();
                            snackbar = Snackbar
                                    .make(lt_signup, "Authentication failed", Snackbar.LENGTH_LONG);

                            snackbar.show();

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  Toast.makeText(this, "Yay", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(this, "sus", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //      Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();

    }

    private void isOnline() {

    }

}