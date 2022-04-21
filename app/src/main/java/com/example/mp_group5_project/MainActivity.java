package com.example.mp_group5_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp_group5_project.sql.Database;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mp_group5_project.databinding.ActivityMainBinding;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mainViewModel;

    private ActivityMainBinding binding;
    private final String TAG = "Main";

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Database db = new Database(this);
        mainViewModel.setCurrentUser(db.getCurrentUser());
        mainViewModel.setCurrentUserImages(db.getUserImages());
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, view.getParent().toString());
                Snackbar.make(view, "E-mail has been sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_setting)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                TextView navHeaderUsernameTV = drawerView.findViewById(R.id.navHeaderUsernameTV);
                TextView navHeaderEmailTV = drawerView.findViewById(R.id.navHeaderEmailTV);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    navHeaderUsernameTV.setText(mainViewModel.getUser().getValue().getUsername());
                    navHeaderEmailTV.setText(mainViewModel.getUser().getValue().getEmail());
//                    navHeaderUsernameTV.setText(new Date(Instant.now().toEpochMilli()).toString());
                }
                SharedPreferences pref = getSharedPreferences("image_path", Context.MODE_PRIVATE);
                if(pref.contains("profilePhotoPath")) {
                    String currentPhotoPath = pref.getString("profilePhotoPath", "");
                    if(currentPhotoPath.isEmpty() == false) {
                        File imgFile = new File(currentPhotoPath);
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ImageView frontImgHead = (ImageView) drawerView.findViewById(R.id.navHeaderImageView);
                        frontImgHead.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d("Request Code", "" + requestCode);
        switch (requestCode) {
            case 101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mainViewModel.setCameraAllowed(true);
                }  else {
                    Toast.makeText(getApplicationContext(), "Please Allow Camera Usage!", Toast.LENGTH_LONG).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                goToLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences loginPref = getSharedPreferences("login_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.clear();
        editor.putBoolean("logout_status", true);
        editor.commit();
        startActivity(intent);
        finish();
    }
}