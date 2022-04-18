package com.example.mp_group5_project.ui.setting;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp_group5_project.MainViewModel;
import com.example.mp_group5_project.R;
import com.example.mp_group5_project.sql.User;
import com.example.mp_group5_project.sql.UserDBHandler;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingFragment extends Fragment {

    ActivityResultLauncher<String> mPermissionResult;
    ActivityResultLauncher<Intent> startActivityResultLauncher;
    CircularImageView profileVImage;
    CircularImageView addVImage;
    Bitmap bitmap;
    SharedPreferences pref;
    String currentPhotoPath;

    EditText mobileET;
    EditText nameET;
    EditText emailET;
    EditText usernameET;
    EditText passwordET;

    TextView headerUsernameTV;
    TextView headerEmailTV;

    ImageButton saveImgBtn;
    UserDBHandler userDB;
    MainViewModel settingViewModel;

    public static final String TAG = "SettingFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDB = new UserDBHandler(getActivity());
        pref = getActivity().getSharedPreferences("image_path", Context.MODE_PRIVATE);

        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            Log.e(TAG, "onActivityResult: PERMISSION GRANTED");
                            doOperationTakePhoto();
                        } else {
                            Log.e(TAG, "onActivityResult: PERMISSION DENIED");
                            Toast.makeText(getActivity(), "Cannot proceed because user not allowing camera uses", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        startActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Bundle extras = data.getExtras();
                            bitmap = (Bitmap) extras.get("data");
                            saveImagePhoto();
                        }
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        profileVImage = (CircularImageView) view.findViewById(R.id.img_profile);
        addVImage = (CircularImageView) view.findViewById(R.id.img_plus);
        saveImgBtn = (ImageButton) view.findViewById(R.id.saveImageButton);
        mobileET = (EditText) view.findViewById(R.id.mobileProfileEditText);
        nameET = (EditText) view.findViewById(R.id.nameProfileET);
        emailET = (EditText) view.findViewById(R.id.emailProfileEditText);
        usernameET = (EditText) view.findViewById(R.id.usernameProfileET);
        passwordET = (EditText) view.findViewById(R.id.passwordProfileET);
        headerUsernameTV = (TextView) view.findViewById(R.id.usernameHeadProfileTV);
        headerEmailTV = (TextView) view.findViewById(R.id.emailHeadProfileTV);
        usernameET.setEnabled(false);
        setProfilePhoto();

        settingViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        settingViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                nameET.setText(user.getName());
                mobileET.setText(user.getPhoneNo());
                passwordET.setText(user.getPassword());
                usernameET.setText(user.getUsername());
                headerUsernameTV.setText(user.getUsername());
                emailET.setText(user.getEmail());
                headerEmailTV.setText(user.getEmail());
            }
        });

        addVImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPermissionResult.launch(Manifest.permission.CAMERA);
            }
        });

        saveImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });

        ImageButton showImgBtn = (ImageButton) view.findViewById(R.id.showPwdBtn);
        showImgBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Log.d(TAG, "onTouch: ACTION DOWN");
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG, "onTouch: ACTION UP");
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                return false;
            }


        });

        return view;
    }

    private void setProfilePhoto() {
        if(pref.contains("profilePhotoPath")) {
            currentPhotoPath = pref.getString("profilePhotoPath", "");
            if(currentPhotoPath.isEmpty() == false) {
                File imgFile = new File(currentPhotoPath);
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileVImage.setImageBitmap(bitmap);
            }
        }
    }

    private void doOperationTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityResultLauncher.launch(takePictureIntent);
    }

    public void saveImagePhoto(){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "profilephoto.jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getActivity(), destination.toString(), Toast.LENGTH_LONG).show();
        Log.i(TAG, destination.toString());
        currentPhotoPath = destination.toString();
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putString("profilePhotoPath", currentPhotoPath);
        editor.commit();
        setProfilePhoto();
    }

    public void saveUserData() {
        userDB.UpdateUserDetails(nameET.getText().toString(), emailET.getText().toString(), mobileET.getText().toString(), passwordET.getText().toString());
        settingViewModel.setCurrentUser(userDB.getCurrentUser());

        Toast.makeText(getContext(), "Save User Done!", Toast.LENGTH_LONG);
    }
}