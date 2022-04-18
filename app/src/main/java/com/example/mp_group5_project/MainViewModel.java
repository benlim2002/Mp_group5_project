package com.example.mp_group5_project;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_group5_project.sql.User;

public class MainViewModel extends ViewModel {
    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    private MutableLiveData<User> currentUser;

    public MainViewModel() {
        currentUser = new MutableLiveData<>();
    }

    public LiveData<User> getUser() { return currentUser; }
}
