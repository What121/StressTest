package com.bestom.stresstest.service;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WSClient extends WebSocketClient {
    private static final String TAG = "WSClient";

    private static URI serveruri;
    public static volatile int i = 0;

    private static volatile List<clientmsg> ClientmsgList =new ArrayList<clientmsg>();

    private static volatile WSClient instance;

    static {
        try {
//            wss://api.cn2.ilifesmart.com:8443/wsapp/
//            ws://121.40.165.18:8800
            serveruri = new URI("ws://192.168.1.110:2333");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public static WSClient getInstance() {
        if (instance == null) {
            synchronized (WSClient.class) {
                if (instance == null) {
                    instance = new WSClient(serveruri);
                }
            }
        }
        return instance;
    }


    public WSClient(URI serverUri) {

        super(serverUri);
    }

    private WSClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    private WSClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    private WSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    private WSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    public WSClient notifaction(clientmsg clientmsg){
        ClientmsgList.add(clientmsg);
        Log.i(TAG, "notifaction: register wsclient msg observable");
        return instance;
    }

    public void removenotifaction(clientmsg clientmsg){
        ClientmsgList.remove(clientmsg);
        Log.i(TAG, "removenotifaction: remove wsclient msg observable");
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        //连接成功
        i = 0;

        //region    发送认证
//        NetBean netBean=new NetBean();
//        netBean.setTIME(TimeUtil.getCurTime());
//        String sign= MD5.stringToMD5(netBean.getSignString()) ;
//
//        String request="{\"id\":1001,\"method\":\"WbAuth\",\"system\":{\"ver\":\"1.0\",\"lang\":\"en\",\"userid\":\""+netBean.getUSERID()+"\",\"appkey\":\""+netBean.getAPPKEY()+"\",\"time\":"+netBean.getTIME()+",\"sign\":\""+sign+"\"}}";
//        Log.i(TAG, "onOpen: request"+request);

//        发送认证
//        send(request);

        //endregion

        if (ClientmsgList.size()>0){
            for (int i=0;i<ClientmsgList.size();i++){
                ClientmsgList.get(i).onOpen(serverHandshake);
                removenotifaction(ClientmsgList.get(i));
                ClientmsgList.remove(i);
            }
        }


        Log.i(TAG, "onOpen: ");
    }

    @Override
    public void onMessage(String s) {
        Log.i(TAG, "onMessage: "+s);
        if (ClientmsgList.size()>0){
            for (int i=0;i<ClientmsgList.size();i++){
                ClientmsgList.get(i).onMsg(s);
                removenotifaction(ClientmsgList.get(i));
            }
        }
    }


    @Override
    public void onClose(int i, String s, boolean b) {
        if (ClientmsgList.size()>0){
            for (int j=0;j<ClientmsgList.size();j++){
                ClientmsgList.get(j).onClose(i, s, b);
                removenotifaction(ClientmsgList.get(j));
            }
        }
        Log.i(TAG, "onClose: " + s);
    }

    @Override
    public void onError(Exception e) {

        //region 处理error
//        String message = e.getMessage();
//        if (message != null) {
//            if (message.indexOf("Host is unresolved") == 0) {
//                Log.i(TAG, "onError: " + message + "请检查网络连接状态和服务器" + serveruri + "状态！");
//                if (i < 3) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                i++;
//                                Log.i(TAG, "reconnection: 20s 后 尝试第" + i + "次重新连接！！！");
//                                Thread.sleep(20000);
////                                WSClient.getInstance().connect();
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }).start();
//                } else {
//                    Log.i(TAG, "onError: " + message+"重连次数已达3次");
//                }
//            }else {
//                Log.i(TAG, "onError: " + message);
//            }
//        }else {
//            Log.i(TAG, "onError: message is null");
//        }
        //endregion
        Log.i(TAG, "onError: " + e.getMessage());
        e.printStackTrace();

//        this.close();

    }



}
