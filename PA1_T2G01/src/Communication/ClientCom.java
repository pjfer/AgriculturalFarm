package Communication;

import java.io.*;
import java.net.*;

/**
 * Class responsible for establishing the communication channel between 
 * a client and a server, on the client side.
 * The communication is made through the TCP protocol.
 */
public class ClientCom {
    /**
     *  Communication Socket to send messages to and receive messages from.
     *  @serialField commSocket
     */
    private Socket commSocket = null;
    
    /**
     *  Server's address that we want to connect to.
     *  @serialField serverHostName
     */
    private final String serverHostName;
    
    /**
     *  Server's port that we want to connect to.
     *  @serialField serverPortNumb
     */
    private final int serverPortNumb;
    
    /**
     *  Input stream of the communication channel.
     *  @serialField in
     */
    private ObjectInputStream in = null;
    
    /**
     *  Output stream of the communication channel.
     *  @serialField out
     */
    private ObjectOutputStream out = null;
    
    /**
     *  Instantiation of a communication channel.
     *
     *  @param hostName server's address that we want to connect to.
     *  @param portNumb server's port that we want to connect to.
     */
    public ClientCom(String hostName, int portNumb) {
        serverHostName = hostName;
        serverPortNumb = portNumb;
    }
    
    /**
     *  Open a communication channel between the client and the server, 
     *  through the instantiation of a communication socket and the opening
     *  of the input and output streams.
     *
     *  @return true, if the communication channel was correctly open.
     *          false, otherwise.
     */
    public boolean open() {
        boolean success = true;
        SocketAddress serverAddress = 
                new InetSocketAddress(serverHostName, serverPortNumb);
        
        try {
            commSocket = new Socket();
            commSocket.connect(serverAddress);
        }
        catch(UnknownHostException e) {
            System.err.println(Thread.currentThread().getName() +
                    " - unknown host: " + serverHostName + "!");
            System.exit(1);
        }
        catch(NoRouteToHostException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unreacheable host: " + serverHostName + "!");
            System.exit(1);
        }
        catch(ConnectException e) {
            System.err.println(Thread.currentThread().getName() +
                " - no response from: " + serverHostName + "." + serverPortNumb 
                    + "!");
        
            if(e.getMessage().equals("Connection refused"))
                success = false;
            else {
                System.err.println(e.getMessage() + "!");
                System.exit(1);
            }
        }
        catch(SocketTimeoutException e) {
            System.err.println(Thread.currentThread().getName() +
                " - timeout from: " + serverHostName + "." + serverPortNumb 
                    + "!");
            success = false;
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to connect to: " + serverHostName + "." + 
                    serverPortNumb + "!");
            System.exit(1);
        }
        
        if(!success) return success;
        
        try {
            out = new ObjectOutputStream(commSocket.getOutputStream());
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to open the output stream!");
            System.exit(1);
        }
        
        try {
            in = new ObjectInputStream(commSocket.getInputStream());
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to open the input stream!");
            System.exit(1);
        }
        
        return success;
    }
    
    /**
     *  Close the input and output streams, as well as the communication socket.
     */
    public void close() {
        try {
            in.close();
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to close the input stream!");
            System.exit(1);
        }
        
        try {
            out.close();
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to close the output stream!");
            System.exit(1);
        }
        
        try {
            commSocket.close();
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to close the communication socket!");
            System.exit(1);
        }
    }
    
    /**
     *  Read an object from the input stream.
     *
     *  @return object from the input stream. Expected to be a Message.
     */
    public Object readObject() {
        Object fromServer = null;
        
        try {
            fromServer = in.readObject();
        }
        catch(InvalidClassException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to deserialize the input stream object!");
            System.exit(1);
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to read the input stream object!");
            System.exit(1);
        }
        catch(ClassNotFoundException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unknown class from the input stream object!");
            System.exit(1);
        }
        
        return fromServer;
    }
    
    /**
     *  Write an object into the output stream.
     *
     *  @param toServer object to be written into the output stream.
     *  Expected to be a Message.
     */
    public void writeObject(Object toServer) {
        try {
            out.writeObject(toServer);
        }
        catch(InvalidClassException e) {
            System.err.println(Thread.currentThread().getName() +
                " - unable to serialize the output stream object!");
            System.exit(1);
        }
        catch(NotSerializableException e) {
            System.err.println(Thread.currentThread().getName() +
                " - output stream object's class is invalid for serialization!");
            System.exit(1);
        }
        catch(IOException e) {
            System.err.println(Thread.currentThread().getName() +
                " - error when writing the object into the output stream!");
            System.exit(1);
        }
    }
}
