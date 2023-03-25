package com.example.localchatting;


import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localchatting.databinding.FragmentFirstBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class JoinChatFragment extends Fragment
{
    private FragmentFirstBinding binding;
    private static final String IPV4_PATTERN_ALLOW_LEADING_ZERO = "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";
    int duration = Toast.LENGTH_SHORT;


    private boolean isNetworkAvailable()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = new NetworkInfo[0];
        try
        {
            netInfo = cm.getAllNetworkInfo();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public View onCreateView(@NotNull
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public boolean isPort(String value)
    {
        if (value.isEmpty())
        {
            return false;
        }

        if (value.matches("\\d+(?:\\.\\d+)?"))
        {
            return Integer.parseInt(value) < 65536;
        }
        return false;
    }

    public String MoveToChat(boolean checkIP, boolean checkPort)
    {
        if (isNetworkAvailable())
        {
            if (!checkIP || !checkPort)
            {
                return "IP or port is not valid!";
            }
        }
        else
        {
            return "Internet connection is not available!";
        }
        return "Connect successful";
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

            Toast toast = Toast.makeText(getActivity(), MoveToChat(valid, isPort(binding.editTextPort.getText().toString())), duration);
            toast.show();


            if (MoveToChat(valid,isPort(binding.editTextPort.getText().toString())).equals("Connect successful"))
            {
                NavHostFragment.findNavController(JoinChatFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

}