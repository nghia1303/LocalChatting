package com.example.localchatting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyDiffUtilCallBack extends DiffUtil.Callback
{
    ArrayList<Message> newList;
    ArrayList<Message> oldList;

    public MyDiffUtilCallBack(ArrayList<Message> newList, ArrayList<Message> oldList)
    {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize()
    {
        if (oldList == null)
        {
            return 0;
        }
        return oldList.size();
    }

    @Override
    public int getNewListSize()
    {
        if (newList == null)
        {
            return 0;
        }
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition)
    {
//        Message newMessages = newList.get(newItemPosition);
//        Message oldMessages = oldList.get(oldItemPosition);
//
//        Bundle diff = new Bundle();
//
//        if (newMessages.id != (oldMessages.price)) {
//            diff.putInt("price", newModel.price);
//        }
//        if (diff.size() == 0) {
//            return null;
//        }
//        return diff;
        return super.getChangePayload(oldItemPosition, newItemPosition);

    }
}
