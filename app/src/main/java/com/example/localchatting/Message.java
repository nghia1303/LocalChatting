package com.example.localchatting;

public class Message
{
    private String message;
    public int id;
    public Message(int id, String message)
    {
        this.id = id;
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
