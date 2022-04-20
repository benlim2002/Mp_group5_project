package com.example.mp_group5_project;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_group5_project.sql.User;

public class MainViewModel extends ViewModel {

    private MutableLiveData<User> currentUser;
    private MutableLiveData<Integer> currentFilter;

    public MainViewModel() {
        currentUser = new MutableLiveData<>();
        currentFilter = new MutableLiveData<>();
        currentFilter.setValue(R.id.filter0);
    }

    public LiveData<User> getUser() { return currentUser; }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public LiveData<Integer> getFilter() { return currentFilter; }

    public void setCurrentFilter(Integer i) {
        currentFilter.setValue(i);
    }
}
