package com.example.localchatting;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localchatting.databinding.FragmentSecondBinding;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatFragment extends Fragment
{
    private MessageAdapter messageAdapter;
    private ArrayList<Message> mListMessage;
    private FragmentSecondBinding binding;
    private int id_message = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String SERVER_IP;
    private int SERVER_PORT;
    Thread Thread1 = null;
    String message;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if(bundle != null){
            // handle your code here.
            SERVER_IP = bundle.getString("IP_ADDRESS");
            SERVER_PORT = bundle.getInt("PORT_NUMBER");
        }
        Thread1 = new Thread(new Thread1());
        Thread1.start();
    }

    @Override
    public View onCreateView(@NotNull
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {

        binding = FragmentSecondBinding.inflate(inflater, container, false);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvMessage.setLayoutManager(linearLayoutManager);
        mListMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        messageAdapter.setData(mListMessage);
        binding.rcvMessage.setAdapter(messageAdapter);


        binding.btnSend.setOnClickListener(v -> {
            message = binding.edtMessage.getText().toString().trim();
            new Thread(new ClientMessageThread(message));
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

    private PrintWriter output;
    private BufferedReader input;



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
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

        alertDialog.setPositiveButton("Có", (dialog, which) -> NavHostFragment.findNavController(ChatFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));
        alertDialog.setNegativeButton("Không", (dialog, which) ->
        {
        });
        alertDialog.show();
    }

    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mHandler.post(() -> {

                });
                new Thread(new ReceiveServerMessage()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveServerMessage implements Runnable
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
                        mHandler.post(() -> {
                           mListMessage.add(new Message(id_message++, message));
                           messageAdapter.setData(mListMessage);
                           binding.rcvMessage.setAdapter(messageAdapter);
                        });
                    }
                    else
                    {
                        new Thread(new Thread1()).start();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class ClientMessageThread implements Runnable
    {
        private String message;
        ClientMessageThread(String message)
        {
            this.message = message;
        }
        @Override
        public void run()
        {
            output.write(message);
            output.flush();
            mHandler.post(() -> {
                mListMessage.add(new Message(id_message++, message));
                messageAdapter.setData(mListMessage);
                binding.rcvMessage.setAdapter(messageAdapter);
            });
        }
    }
}