package com.example.mp_group5_project.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mp_group5_project.MainActivity;
import com.example.mp_group5_project.MainViewModel;
import com.example.mp_group5_project.R;
import com.example.mp_group5_project.databinding.FragmentGalleryBinding;
import com.example.mp_group5_project.ui.home.HomeFragment;

public class GalleryFragment extends Fragment {

    private MainViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    private final String TAG = "Gallery";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel =
                new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        final TextView galleryText = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                galleryText.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}