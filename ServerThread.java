import java.net.*;
import java.io.*;

public class ServerThread extends Thread
{  private Server       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private Thread thread;

   public ServerThread(Server _server, Socket _socket)
   {
	  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();

   }
   public void send(String msg)
   {
	   try{
		   streamOut.writeUTF(msg);
         streamOut.flush();
       }
       catch(IOException ioe)
       {
		  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          thread=null;
       }
   }
   
   public int getID(){
	   return ID;
   }

   public void run()
   {
	  System.out.println("Server Thread " + ID + " running.");
	  thread = new Thread(this);
      while (true){
		 try{
         String message = streamIn.readUTF();
         server.broadcast(ID, message);

         // Check the message for "fail" or "success"
         if (message.contains("fail") || message.contains("success")) {
               server.saleMessage(ID, message);
         }
         
		 } catch(IOException ioe){
			//System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            thread = null;
         }
      }
   }

   public void open() throws IOException
   {
	  streamIn = new DataInputStream(new
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }

   public void close() throws IOException
   {
	   if (socket != null)
	   	socket.close();

      if (streamIn != null)
      	streamIn.close();

      if (streamOut != null)
      	streamOut.close();
   }
}