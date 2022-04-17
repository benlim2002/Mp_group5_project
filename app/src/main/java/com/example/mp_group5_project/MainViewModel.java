package com.example.mp_group5_project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_group5_project.sql.User;

public class MainViewModel extends ViewModel {
    private MutableLiveData<User> currentUser;
    private MutableLiveData<String> mText;

    public MainViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Main View Model mText");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
