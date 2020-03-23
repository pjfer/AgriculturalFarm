package Communication;

import java.io.*;
import java.net.*;

/**
 * Class responsible for establishing the communication channel between 
 * a client and a server, on the server side.
 * The communication is made through the TCP protocol.
 */
public class ServerCom {
  /**
   * Communication Socket that is listening and waiting for a client's message.
   * @serialField listeningSocket
   */
   private ServerSocket listeningSocket = null;

  /**
   * Communication Socket to send messages to and receive messages from.
   * @serialField commSocket
   */
   private Socket commSocket = null;

  /**
   * Server's port number to listen to.
   * @serialField serverPortNumb
   */

   private final int serverPortNumb;
  /**
   * Input stream of the communication channel.
   * @serialField in
   */
   private ObjectInputStream in = null;

  /**
   * Output stream of the communication channel.
   * @serialField out
   */
   private ObjectOutputStream out = null;

  /**
   * Instantiation of a communication channel without a given server socket.
   *
   * @param portNumb server's port number to listen to.
   */
   public ServerCom(int portNumb) {
      serverPortNumb = portNumb;
   }

  /**
   * Instantiation of a communication channel with a given server socket.
   *
   * @param portNumb server's port number to listen to.
   * @param lSocket socket to listen to.
   */
   public ServerCom(int portNumb, ServerSocket lSocket) {
      serverPortNumb = portNumb;
      listeningSocket = lSocket;
   }

  /**
   * Service establishment, through the instantiation of a listening socket to
   * the local address and given port.
   */
   public void start() {
      try {
          listeningSocket = new ServerSocket(serverPortNumb);
          setTimeout(1000);
      }
      catch(BindException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unable to connect to port: " + serverPortNumb + "!");
            System.exit(1);
      }
      catch(IOException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unexpected error when connecting to port: " + 
                  serverPortNumb + "!");
            System.exit(1);
      }
   }

  /**
   * Close the established service and the listening socket.
   */
   public void end() {
      try {
          listeningSocket.close();
      }
      catch(IOException e) {
          System.err.println(Thread.currentThread().getName() +
              " - unable to close the socket!");
          System.exit(1);
      }
   }

  /**
   * Open a communication socket to listen for a pending request from a client, 
   * through the instantiation of a communication socket, as well as the opening
   * of input and output streams.
   *
   * @return server's communication socket.
   * @throws java.net.SocketTimeoutException when a timeout occurs.
   */
   public ServerCom accept() throws SocketTimeoutException {
      ServerCom scon;

      scon = new ServerCom(serverPortNumb, listeningSocket);
      
      try {
          scon.commSocket = listeningSocket.accept();
      }
      catch(SocketTimeoutException e) {
          throw new SocketTimeoutException("Timeout!");
      }
      catch(SocketException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unexpected closure of the communication socket!");
          System.exit(1);
      }
      catch(IOException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unable to open a communication socket!");
          System.exit(1);
      }

      try {
          scon.in = new ObjectInputStream(scon.commSocket.getInputStream());
      }
      catch(IOException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unable to open the input stream!");
          System.exit(1);
      }
      
      try {
          scon.out = new ObjectOutputStream(scon.commSocket.getOutputStream());
      }
      catch(IOException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unable to open the output stream!");
          System.exit(1);
      }

      return scon;
   }

  /**
   * Close the communication socket, as well as the input and output streams.
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
   * Definition of a server's timeout.
   * 
   * @param time amount of time, in milliseconds, that the server timeouts.
   */
   public void setTimeout(int time) {
      try {
          listeningSocket.setSoTimeout(time);
      }
      catch(SocketException e) {
          System.err.println(Thread.currentThread().getName() +
                  " - unexpected error when timing out the server socket!");
          System.exit(1);
      }
   }

  /**
   * Read a object from the input stream.
   *
   * @return object from the input stream. Expected to be a Message.
   */
   public Object readObject() {
      Object fromClient = null;

      try {
          fromClient = in.readObject();
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

      return fromClient;
   }

  /**
   *  Write an object into the output stream.
   *
   *  @param toClient object to be written into the output stream.
   *  Expected to be a Message.
   */
   public void writeObject(Object toClient) {
      try {
          out.writeObject(toClient);
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
