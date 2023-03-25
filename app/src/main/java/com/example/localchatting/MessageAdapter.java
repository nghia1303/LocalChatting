package com.example.localchatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private ArrayList<Message> mListMessage;

    public void setData(ArrayList<Message> newMListMessage)
    {
        MyDiffUtilCallBack diffUtil = new MyDiffUtilCallBack(mListMessage, newMListMessage);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mListMessage = newMListMessage;
        diffResult.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        Message message = mListMessage.get(position);
        if (message == null)
        {
            return;
        }
        holder.tvMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount()
    {
        if (mListMessage != null)
        {
            return mListMessage.size();
        }
        return 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvMessage;

        public MessageViewHolder(@NotNull View itemView)
        {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvChat);
        }
    }


}
