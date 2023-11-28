package com.nhnacademy.wire;

import com.nhnacademy.message.Message;

public interface Wire {

    public void put(Message message);

    public boolean hasMessage();

    public Message get();
}
