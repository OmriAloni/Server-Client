package bgu.spl181.net.impl.generalImpls;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.Json.JsonMovie;
import bgu.spl181.net.impl.Json.JsonUser;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class connectionImpl<T> implements Connections {

    private ConcurrentHashMap<Integer, ConnectionHandler> clientsDataBase = new ConcurrentHashMap<>();//All clients
    private ConcurrentHashMap<Integer, String> loggedInClients = new ConcurrentHashMap<>();//All clients
    private JsonUser userDataBase;
    private JsonMovie movieDataBase;

    @Override
    public boolean send(int connectionId, Object msg) {
        if (!this.clientsDataBase.containsKey(connectionId))
            return false;
        else {//exists
            this.clientsDataBase.get(connectionId).send(msg);
            return true;
        }//end of else
    }

    @Override
    public void broadcast(Object msg) {

        Collection<Integer> ActiveClients = this.loggedInClients.keySet();
        ConnectionHandler connectedClient;
        for (Integer activeClient : ActiveClients) {
            connectedClient = this.getClientsDataBase().get(activeClient);
            connectedClient.send("BROADCAST " + msg);
        }

    }

    @Override
    public void disconnect(int connectionId, String UserName) {

        boolean exists = this.clientsDataBase.containsKey(connectionId);

        if (exists) {
            ConnectionHandler disconnect = this.clientsDataBase.get(connectionId);
            try {
                disconnect.close();
                clientsDataBase.remove(connectionId, UserName);
            } catch (IOException ignored) {
                ignored.printStackTrace();//TODO delete before submitting
            }

        }//end of if

    }

    @Override
    public boolean AddConnection(int connectionId, ConnectionHandler handler) {
        if (!this.clientsDataBase.contains(connectionId)) {
            this.clientsDataBase.put(connectionId, handler);
        }
        return this.clientsDataBase.contains(connectionId);//true - added/exists false not exists
    }

    public boolean isLoggedIn(int conId, String UserName) {
        return this.loggedInClients.containsKey(conId) || this.loggedInClients.containsValue(UserName);
    }

    public boolean isLoggedIn(int conId) {
        return this.loggedInClients.containsKey(conId);
    }
    public void SetJson(JsonUser userDataBase, JsonMovie movieDataBase){
        this.userDataBase = userDataBase;
        this.movieDataBase = movieDataBase;
    }

    public JsonUser getUserDataBase() {
        return userDataBase;
    }

    public JsonMovie getMovieDataBase() {
        return movieDataBase;
    }

    public ConcurrentHashMap<Integer, String> getLoggedInClients() {
        return loggedInClients;
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getClientsDataBase() {
        return clientsDataBase;
    }
}
