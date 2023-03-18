package com.example.localchatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localchatting.databinding.FragmentFirstBinding;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstFragment extends Fragment
{
    private FragmentFirstBinding binding;
    private static final String IPV4_PATTERN_ALLOW_LEADING_ZERO = "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";
    CharSequence text;
    int duration = Toast.LENGTH_SHORT;


    @Override
    public View onCreateView(@NotNull
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
//        EditText IP = findViewById(R.id.editTextIP);
//        EditText port = findViewById(R.id.editTextIP);
//        Button btn = findViewById(R.id.button);



        view.findViewById(R.id.button).setOnClickListener(view1 -> {
            Pattern pattern = Pattern.compile(IPV4_PATTERN_ALLOW_LEADING_ZERO);
            Matcher match = pattern.matcher(binding.editTextIP.getText().toString());
            boolean valid = match.matches();
            text = (valid) ? "IP valid!" : "IP not valid";
            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
        });
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

}