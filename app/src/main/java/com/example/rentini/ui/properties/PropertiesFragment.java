package com.example.rentini.ui.properties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rentini.databinding.FragmentPropertiesBinding;

public class PropertiesFragment extends Fragment {

private FragmentPropertiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        PropertiesViewModel propertiesViewModel =
                new ViewModelProvider(this).get(PropertiesViewModel.class);

    binding = FragmentPropertiesBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textProperties;
        propertiesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}