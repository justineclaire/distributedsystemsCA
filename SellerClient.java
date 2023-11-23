import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.lang.reflect.Array;

public class SellerClient implements Runnable
{  private Socket socket              = null;
   private Thread thread              = null;
   private BufferedReader  console   = null;
   private DataOutputStream streamOut = null;
   private DataInputStream  streamIn = null;
   private SaleThread salethread    = null;
   private ArrayList<Integer> stock;
   final int[] saleItem = {0};// index for selling every 60 seconds
   private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   private int saleInProg = 0;

   public SellerClient()
   {
     //Default server details 
     String serverName = "localhost";
     int serverPort = 4444;
     stock = new ArrayList<Integer>();

     //Default stock for sellers to sell - in the order potatoes, oil, flour, sugar
      stock.add(20); // 20kg potatoes
      stock.add(20); // 20 litres oil
      stock.add(20); // 20kg flour
      stock.add(20); // 20kg sugar

	  System.out.println("Establishing connection. Please wait ...");

      try{
		 socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         System.out.println("Welcome to the market Seller!");
         start();

         //broadcast what is on sale every 60 seconds
         scheduler.scheduleAtFixedRate(() -> {
            broadcastStock();
         }, 0, 60, TimeUnit.SECONDS); 
      }
      catch(UnknownHostException uhe){
		  System.out.println("Host unknown: " + uhe.getMessage());
	  }
      catch(IOException ioe){
		  System.out.println("Unexpected exception: " + ioe.getMessage());
	  }
   }

   public ArrayList<Integer> getStock() {
      return stock;
   }


   public void broadcastStock() {
      //broadcast what is on sale every 60 seconds
      try {
            if (saleItem[0] == 0) {
               streamOut.writeUTF(stock.get(saleItem[0])+"kg of potatoes for sale");
               System.out.println(stock.get(saleItem[0])+"kg of potatoes for sale");
               // show countdown
               for(int i = 60; i >= 0; i-=10) {
                  System.out.println(i + " seconds left");
                  try {
                     Thread.sleep(10000); // Sleep for 1 second before sending the next message
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               streamOut.flush();
               saleItem[0]++;
            } else if (saleItem[0] == 1) {
               streamOut.writeUTF(stock.get(saleItem[0])+"l of oil for sale");
               System.out.println(stock.get(saleItem[0])+"l of oil for sale");
               // show countdown
               for(int i = 60; i >= 0; i-= 10) {
                  System.out.println(i + " seconds left");
                  try {
                     Thread.sleep(10000); // Sleep for 1 second before sending the next message
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               streamOut.flush();
               saleItem[0]++;
            } else if (saleItem[0] == 2) {
               streamOut.writeUTF(stock.get(saleItem[0])+"kg of flour for sale");
               System.out.println(stock.get(saleItem[0])+"kg of flour for sale");
               // show countdown
               for(int i = 60; i >= 0; i-= 10) {
                  System.out.println(i + " seconds left");
                  try {
                     Thread.sleep(10000); // Sleep for 1 second before sending the next message
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               streamOut.flush();
               saleItem[0]++;
            } else if (saleItem[0] == 3) {
               streamOut.writeUTF(stock.get(saleItem[0])+"kg of sugar for sale");
               System.out.println(stock.get(saleItem[0])+"kg of sugar for sale");
               // show countdown
               for(int i = 60; i >= 0; i-= 10) {
                  System.out.println(i + " seconds left");
                  try {
                     Thread.sleep(10000); // Sleep for 10 second before sending the next message
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               streamOut.flush();
               saleItem[0] = 0;
            }
            
      } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Sending error: " + ioe.getMessage());
            stop();
      }
         
   }

    public void run()
   {
	   while (thread != null && saleInProg == 0){
         try {
            System.out.println("Press q to leave the market");
            String message = console.readLine();
            if (message.equals("q")) {
               stop();
            }
         
           }
           catch(IOException ioe)
           {  System.out.println("Sending error: " + ioe.getMessage());
              stop();
           }
        }
   }


   public void start() throws IOException
   {
	   console = new BufferedReader(new InputStreamReader(System.in));
      streamIn  = new DataInputStream(socket.getInputStream());
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null) {
         salethread = new SaleThread(this, socket);
         thread = new Thread(this);
         thread.start();

      }
   }

   public void stop()
   {
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {
		  System.out.println("Error closing ...");

      }
      System.exit(0);
      thread = null;
   }


   public static void main(String args[])
   {  SellerClient client = null;
      client = new SellerClient();
   }

   public synchronized void messageFromSale(int ID, int index, int quantity) throws IOException {
      if (stock.get(index) >= quantity) {
         System.out.println("Sold " + quantity + " of item " + (index+1) + " to buyer " + ID);
         stock.set(index, stock.get(index) - quantity);
         System.out.println("New stock: " + stock);
         streamOut.writeUTF("success buyer "+ID + "! you have bought " + quantity + " of item ");
         streamOut.flush();
      } else {
         System.out.println("Sale unsuccessful no stock left");
         streamOut.writeUTF("fail"+ID + ": could not complete purchase, no stock left");
         streamOut.flush();
      }
      
   }
}
