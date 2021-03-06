/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.api.bidi;

import bgu.spl181.net.impl.Json.JsonUser;
import bgu.spl181.net.impl.generalImpls.connectionImpl;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

/**
 *
 * @author bennyl
 */
public interface BidiMessagingProtocol<T>  {

    void start(int connectionId, connectionImpl<T> connections, ConnectionHandler handler);
    
    void process(T message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();

}
