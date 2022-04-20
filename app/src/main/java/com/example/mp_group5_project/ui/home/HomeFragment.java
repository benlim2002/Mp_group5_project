package com.example.mp_group5_project.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mp_group5_project.MainViewModel;
import com.example.mp_group5_project.R;
import com.example.mp_group5_project.databinding.FragmentHomeBinding;
import com.example.mp_group5_project.sql.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private FrameLayout layout;
    private CameraRenderer renderer;
    private TextureView textureView;
    private int filterId = R.id.filter0;
    private int mCurrentFilterId = 0;
    private final String TAG = "HomeFragment";
    ActivityResultLauncher<String> mPermissionResult;

    String[] TITLES = {"Original", "EdgeDetection", "Pixelize",
            "EMInterference", "TrianglesMosaic", "Legofied",
            "TileMosaic", "Blueorange", "ChromaticAberration",
            "BasicDeform", "Contrast", "NoiseWarp", "Refraction",
            "Mapping", "Crosshatch", "LichtensteinEsque",
            "AsciiArt", "MoneyFilter", "Cracked", "Polygonization",
            "JFAVoronoi", "BlackAndWhite", "Gray", "Negative",
            "Nostalgia", "Casting", "Relief", "Swirl", "HexagonMosaic",
            "Mirror", "Triple", "Cartoon", "WaterReflection"
    };

    Integer[] FILTER_RES_IDS = {R.id.filter0, R.id.filter1, R.id.filter2, R.id.filter3, R.id.filter4,
            R.id.filter5, R.id.filter6, R.id.filter7, R.id.filter8, R.id.filter9, R.id.filter10,
            R.id.filter11, R.id.filter12, R.id.filter13, R.id.filter14, R.id.filter15, R.id.filter16,
            R.id.filter17, R.id.filter18, R.id.filter19, R.id.filter20,
            R.id.filter21, R.id.filter22, R.id.filter23, R.id.filter24,
            R.id.filter25, R.id.filter26, R.id.filter27, R.id.filter28,
            R.id.filter29, R.id.filter30, R.id.filter31, R.id.filter32};

    ArrayList<Integer> mFilterArray = new ArrayList<>(Arrays.asList(FILTER_RES_IDS));

    private MainViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        layout = binding.homeLayout;
        setHasOptionsMenu(true);

        homeViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                filterId = integer;
                mCurrentFilterId = mFilterArray.indexOf(filterId);
                if (renderer != null)
                    renderer.setSelectedFilter(filterId);
            }
        });

        View root = binding.getRoot();
        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            Log.e(TAG, "onActivityResult: PERMISSION GRANTED");
                            setupCameraPreviewView();
                        } else {
                            Log.e(TAG, "onActivityResult: PERMISSION DENIED");
                            Toast.makeText(getActivity(), "Cannot proceed because user not allowing camera uses", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), "Camera access is required.", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }

        } else {
            setupCameraPreviewView();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        renderer.stopRunning();
        super.onDestroyView();
        binding = null;
    }

    void setupCameraPreviewView() {
        renderer = new CameraRenderer(getActivity());
        textureView = new TextureView(getActivity());
        layout.addView(textureView);
        textureView.setSurfaceTextureListener(renderer);
        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                renderer.onSurfaceTextureSizeChanged(null, v.getWidth(), v.getHeight());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Log.d("Menu Item Name", Integer.toString(itemId));
        switch(itemId) {
            case R.id.capture:
                Toast.makeText(getActivity(),
                        capture() ? "The capture has been saved to your sdcard root path." :
                                "Save failed!",
                        Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                return super.onOptionsItemSelected(item);
            case R.id.filters:
                return true;
            default:
                homeViewModel.setCurrentFilter(item.getItemId());
                return true;
        }
    }

    private boolean capture() {
        Date date = new Date();
        SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String timeString = dateformat1.format(date);
        String fileName = TITLES[mCurrentFilterId] + "_" + timeString + ".png";

        String path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File parent = new File(path);
        File destination = new File(parent, fileName);
        Log.d("Parent", parent.getAbsolutePath());
        Log.i("Destination", destination.getAbsolutePath());
        // create bitmap screen capture
        Bitmap bitmap = textureView.getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        FileOutputStream fo;

        try {
            parent.mkdir();
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
               Log.i(" Files [" + i + "]", files[i].getName());
            }
        }
        return true;
    }
}