package com.example.localchatting;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localchatting.databinding.FragmentServerChatBinding;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ServerChatFragment extends Fragment
{
    private FragmentServerChatBinding binding;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> mListMessage;
    private final int id_message = -1;
    private String serverName;
    private int serverPort;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment



        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                exitChat();
            }
        };



        return inflater.inflate(R.layout.fragment_server_chat, container, false);
    }


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

        alertDialog.setPositiveButton("Có", (dialog, which) -> NavHostFragment.findNavController(ServerChatFragment.this)
                .navigate(R.id.action_serverChatFragment_to_FirstFragment));

        alertDialog.setNegativeButton("Không", (dialog, which) ->
        {
        });

        alertDialog.show();
    }





    class ServerThread extends Thread implements Runnable
    {
        private ServerSocket serverSocket;
        private final String  SERVER_IP = getLocalIpAddress();
        private final int SERVER_PORT = 1234;
        private boolean serverRunning = false;
        private Handler mHandler = new Handler(Looper.getMainLooper());

        public ServerThread() throws UnknownHostException
        {
            try
            {
                getLocalIpAddress();
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }

//        private String getServer_ip()
//        {
//            JoinChatFragment joinChatFragment = new JoinChatFragment();
//            WifiManager wifiMan = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInf = wifiMan.getConnectionInfo();
//            int ipAddress = wifiInf.getIpAddress();
//            String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
//            return ip;
//        }

        public String getLocalIpAddress() throws UnknownHostException
        {
            WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
        }

        public int getServerPort()
        {
            return this.SERVER_PORT;
        }

        public String getServerIP()
        {
            return this.SERVER_IP;
        }

        public void startServer()
        {
            serverRunning = true;
            start();
        }

        @Override
        public void run()
        {
            try
            {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                while (serverRunning)
                {
                    Socket socket = serverSocket.accept();
                    mHandler.post(() ->
                    {

                    });
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}