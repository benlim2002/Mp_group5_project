package com.example.mp_group5_project;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_group5_project.sql.User;

public class MainViewModel extends ViewModel {

    private MutableLiveData<User> currentUser;
    private MutableLiveData<Integer> currentFilter;
    private MutableLiveData<String[]> currentUserImages;
    private MutableLiveData<Boolean> cameraAllowed;

    public MainViewModel() {
        currentUser = new MutableLiveData<>();
        currentUserImages = new MutableLiveData<>();
        currentFilter = new MutableLiveData<>();
        cameraAllowed = new MutableLiveData<>();
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

    public LiveData<String[]> getImages() {
        return currentUserImages;
    }

    public void setCurrentUserImages(String[] images) {
        currentUserImages.setValue(images);
    }

    public LiveData<Boolean> getCameraAllowed() { return cameraAllowed; }

    public void setCameraAllowed(Boolean allowed) { cameraAllowed.setValue(allowed); }
}
