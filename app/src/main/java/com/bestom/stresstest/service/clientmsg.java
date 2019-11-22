package com.bestom.stresstest.service;


import org.java_websocket.handshake.ServerHandshake;

public interface clientmsg {


     void onOpen(ServerHandshake serverHandshake);

     void onMsg(String s);

     void onClose(int i, String s, boolean b);

     void onError(Exception e);


}