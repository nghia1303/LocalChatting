package com.example.localchatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localchatting.databinding.FragmentSecondBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class ChatFragment extends Fragment
{
    private MessageAdapter messageAdapter;
    private ArrayList<Message> mListMessage;
    private FragmentSecondBinding binding;
    private int id_message = 0;


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

        binding.btnSend.setOnClickListener(v -> sendMessage());

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

    public void sendMessage()
    {
        String strMessage = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage))
        {
            return;
        }
        mListMessage.add(new Message(id_message++, strMessage));
        messageAdapter.setData(mListMessage);
        binding.rcvMessage.scrollToPosition(mListMessage.size() - 1);
        binding.edtMessage.setText("");
    }


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
}