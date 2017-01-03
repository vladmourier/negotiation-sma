package model.communication;

import model.communication.events.MessageReceivedEvent;
import model.communication.events.MessageReceivedListener;
import model.communication.message.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Vlad on 17/12/2016.
 * The server side of an agentSOcket : it listens to messages and when one comes in, it fires a messageReceivedEvent
 */
public class AgentServer extends AgentConnector implements Runnable{

    // Use CopyOnWriteArrayList to avoid ConcurrentModificationExceptions if a
    // listener attempts to remove itself during event notification.
    private final CopyOnWriteArrayList<MessageReceivedListener> listeners;

    private ServerSocket serverSocket;

    private int agentid;
    private Message lastReceivedMessage;

    AgentServer(int idAgent){
        try {
            this.agentid = idAgent;
            this.serverSocket = new ServerSocket(AgentConnector.BASE_SERVER_PORT + idAgent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listeners = new CopyOnWriteArrayList<>();
    }

    void addMessageReceivedListener(MessageReceivedListener l) {
        this.listeners.add(l);
    }

    public void removeMessageReceivedListener(MessageReceivedListener l) {
        this.listeners.remove(l);
    }

    // Event firing method.  Called internally by other class methods.
    private void fireChangeEvent() {
        MessageReceivedEvent evt = new MessageReceivedEvent(this);

        for (MessageReceivedListener l : listeners) {
            l.messageReceived(evt);
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while(true){
                Socket serverResponseSocket = serverSocket.accept();
                this.IS = serverResponseSocket.getInputStream();
                this.BIS = new BufferedInputStream(this.IS);
                this.OS = serverResponseSocket.getOutputStream();
                this.BOS = new BufferedOutputStream(OS);
                if(this.BIS.read(buffer) != 0){
                    lastReceivedMessage = new Message(new String(buffer));
                    fireChangeEvent();
                }
                this.BIS.close();
                serverResponseSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getAgentid() {
        return agentid;
    }

    public Message getLastReceivedMessage() {
        return lastReceivedMessage;
    }
}
