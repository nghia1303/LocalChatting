package com.example.localchatting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localchatting.databinding.FragmentServerChatBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerChatFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private FragmentServerChatBinding binding;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> mListMessage;
    private int id_message = -1;
    private PrintWriter output;
    private BufferedReader input;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ServerSocket serverSocket;
    private Socket socket;
    private final int REQUEST_ACCESS_WIFI_STATE = 1;
    private String SERVER_IP;
    private int SERVER_PORT;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            Thread serverThread = new Thread(new ServerThread());
            serverThread.start();
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentServerChatBinding.inflate(inflater, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvMessage.setLayoutManager(linearLayoutManager);
        mListMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        binding.rcvMessage.setAdapter(messageAdapter);


        binding.btnSend.setOnClickListener(v -> {
            String message = binding.edtMessage.getText().toString().trim();
            new Thread(new ServerMessageThread(message));
        });

        binding.edtMessage.setOnClickListener(v -> checkKeyboard());
        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                exitChat();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return binding.getRoot();
    }


    private void requestWifiAccess(Context context) throws UnknownHostException
    {
        int permissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_ACCESS_WIFI_STATE);
        } else {
            getIPAddress();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_WIFI_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }


    private String getIPAddress()
    {
        Context context = requireContext().getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

//    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            result ->
//            {
//                if (result) {
//                    WifiManager wifiMan = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    WifiInfo wifiInf = wifiMan.getConnectionInfo();
//                    int ipAddress = wifiInf.getIpAddress();
//                    SERVER_IP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//                } else {
//                    Toast toast = Toast.makeText(getActivity(), "Vui lòng cấp quyền cho ứng dụng!", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//    );



    public void checkKeyboard()
    {
        final View activityChatView = binding.activityChat;
        activityChatView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Rect rect = new Rect();

                activityChatView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = activityChatView.getRootView().getHeight() - rect.height();
                if (heightDiff > 0.25*activityChatView.getRootView().getHeight())
                {
                    if (mListMessage.size() > 0)
                    {
                        binding.rcvMessage.scrollToPosition(mListMessage.size()-1);
                        activityChatView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    private void exitChat()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Thông báo!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Bạn có muốn thoát khỏi cuộc hội thoại không ?");

        alertDialog.setPositiveButton("Có", (dialog, which) -> {
            try
            {
                socket.close();
                serverSocket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            NavHostFragment.findNavController(ServerChatFragment.this).navigate(R.id.action_serverChatFragment_to_FirstFragment);
        });

        alertDialog.setNegativeButton("Không", (dialog, which) ->
        {
        });

        alertDialog.show();
    }

    class ServerThread implements Runnable
    {

        private final String SERVER_IP = getIPAddress();
        private final int SERVER_PORT = 1234;

        public ServerThread() throws UnknownHostException
        {
            getIPAddress();
        }

        public int getServerPort()
        {
            return this.SERVER_PORT;
        }
        public String getServerIP()
        {
            return this.SERVER_IP;
        }


        @Override
        public void run()
        {
            try
            {
                serverSocket = new ServerSocket(SERVER_PORT);
                mHandler.post(() ->
                {
                    binding.connectedIP.setText("Server IP: " + getServerIP());
                    binding.connectedPort.setText("Server port: " + getServerPort());
                    mHandler.post(() ->
                    {
                    });
                });
                try
                {
                    socket = serverSocket.accept();
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    mHandler.post(() ->
                    {
                    });
                    new Thread(new ReceiveClientMessageThread()).start();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private class ReceiveClientMessageThread implements Runnable
    {

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final String message = input.readLine();
                    if (message != null)
                    {
                        mListMessage.add(new Message(id_message++, message));
                        messageAdapter.setData(mListMessage);
                        binding.rcvMessage.setAdapter(messageAdapter);
                    }
                    else
                    {
                        new Thread(new ServerThread()).start();
                        return;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class ServerMessageThread implements Runnable
    {
        private String message;
        ServerMessageThread(String message)
        {
            this.message = message;
        }
        @Override
        public void run()
        {
            output.write(message);
            output.flush();
            mHandler.post(()-> {
                mListMessage.add(new Message(id_message++, message));
                messageAdapter.setData(mListMessage);
                binding.rcvMessage.setAdapter(messageAdapter);
                binding.edtMessage.setText("");
            });
        }
    }
}