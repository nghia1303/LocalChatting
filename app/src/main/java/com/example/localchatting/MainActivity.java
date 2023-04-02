package com.example.localchatting;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localchatting.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        com.example.localchatting.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}