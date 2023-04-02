package com.example.localchatting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localchatting.databinding.FragmentFirstBinding;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinChatFragment extends Fragment
{
    private FragmentFirstBinding binding;
    private static final String IPV4_PATTERN_ALLOW_LEADING_ZERO = "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";
    private final int messageID = -1;

    int duration = Toast.LENGTH_SHORT;
    Pattern pattern = Pattern.compile(IPV4_PATTERN_ALLOW_LEADING_ZERO);
    Matcher match = pattern.matcher(binding.editTextIP.getText().toString());
    boolean valid = match.matches();
    Toast toast = Toast.makeText(getActivity(), MoveToChat(valid, isPort(binding.editTextPort.getText().toString())), duration);
    ChatFragment chatFragment = new ChatFragment();


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

        view.findViewById(R.id.button).setOnClickListener(view1 -> {

            toast.show();
            if (MoveToChat(valid,isPort(binding.editTextPort.getText().toString())).equals("Connect successful"))
            {
//                serverName = String.valueOf(binding.editTextIP.getText());
//                serverPort = Integer.parseInt(String.valueOf(binding.editTextPort.getText()));
//
//                new Thread(() ->
//                {
//                    try
//                    {
//                        Socket socket = new Socket(serverName, serverPort);
//                        MessageAdapter messageAdapter = new MessageAdapter();
//                        BufferedReader br_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                        String message_from_server = br_input.readLine();
//                        mListMessageFromSv.add(new Message(messageID++, message_from_server));
//                        messageAdapter.setData(mListMessageFromSv);
//                        chatFragment.sendMessage(mListMessageFromSv);
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//                });



                NavHostFragment.findNavController(JoinChatFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.regServer.setOnClickListener(v -> NavHostFragment.findNavController(JoinChatFragment.this).navigate(R.id.action_FirstFragment_to_serverChatFragment));
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }


}


