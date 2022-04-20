package com.example.mp_group5_project.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mp_group5_project.MainViewModel;
import com.example.mp_group5_project.R;
import com.example.mp_group5_project.databinding.FragmentGalleryBinding;
import com.example.mp_group5_project.sql.User;

public class GalleryFragment extends Fragment {

    private MainViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private String images[];

    GridView simpleGrid;

    private final String TAG = "Gallery";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel =
                new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        final TextView galleryText = binding.textView;
        galleryViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                galleryText.setText(user.getName() + "'s Gallery");
            }
        });

        galleryViewModel.getImages().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {
                Log.i(TAG, "Length: " + strings.length);
                images = strings;
                simpleGrid = (GridView) root.findViewById(R.id.gridView); // init GridView
                // Create an object of CustomAdapter and set Adapter to GirdView
                CustomAdapter customAdapter = new CustomAdapter(getActivity(), images);
                simpleGrid.setAdapter(customAdapter);
                // implement setOnItemClickListener event on GridView
                simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // set an Intent to Another Activity
                        Intent intent = new Intent(getActivity(), ImageActivity.class);
                        intent.putExtra("image", images[position]); // put image data in Intent
                        startActivity(intent); // start Intent
                    }
                });
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
