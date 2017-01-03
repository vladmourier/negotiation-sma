package model.communication;

import model.communication.events.MessageReceivedListener;

/**
 * Created by Vlad on 17/12/2016.
 * Provides a couple (client, server) to send and listen to requests
 */
public class AgentSocket {

    private AgentServer agentServer;
    private AgentClient agentClient;

    public AgentSocket(int agentId){
        this.agentServer = new AgentServer(agentId);
        new Thread(agentServer).start();//Start listening

        this.agentClient = new AgentClient();
    }

    public void sendMessage(int recipient, String msg){
        this.agentClient.send(recipient, msg);
    }

    public void addMessageReceivedListener (MessageReceivedListener messageReceivedListener){
        this.agentServer.addMessageReceivedListener(messageReceivedListener);
    }

}
