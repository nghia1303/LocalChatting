package com.example.localchatting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localchatting.databinding.FragmentFirstBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinChatFragment extends Fragment
{
    private FragmentFirstBinding binding;
    private static final String IPV4_PATTERN_ALLOW_LEADING_ZERO = "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";
    int duration = Toast.LENGTH_SHORT;
    Pattern pattern = Pattern.compile(IPV4_PATTERN_ALLOW_LEADING_ZERO);
    Toast toast;
    boolean valid;
    String textIP;
    String textPort;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TextView IP = view.findViewById(R.id.editTextIP);
        TextView Port = view.findViewById(R.id.editTextPort);


        view.findViewById(R.id.button).setOnClickListener(view1 -> {


            textIP = IP.getText().toString();
            textPort = Port.getText().toString();
            Matcher match = pattern.matcher(textIP);
            valid = match.matches();
            toast = Toast.makeText(getActivity(), MoveToChat(valid, isPort(String.valueOf(textPort))), duration);

            toast.show();
            if (MoveToChat(valid, isPort(textPort)).equals("Connect successful"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("IP_ADDRESS", getIPAddress());
                bundle.putInt("PORT_NUMBER", getPort());

                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                NavHostFragment.findNavController(JoinChatFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.regServer.setOnClickListener(v -> NavHostFragment.findNavController(JoinChatFragment.this).navigate(R.id.action_FirstFragment_to_serverChatFragment));
    }




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





    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    public String getIPAddress()
    {
        return textIP;
    }

    public int getPort()
    {
        return Integer.parseInt(textPort);
    }
}


