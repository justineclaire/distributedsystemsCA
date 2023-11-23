import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.*;


public class BuyerClient implements Runnable
{  private Socket socket              = null;
   private Thread thread              = null;
   private BufferedReader  console   = null;
   private DataOutputStream streamOut = null;
   private DataInputStream streamIn = null;
   private int purchaseInProg = 0;
   private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


   public BuyerClient()
   {
      //Default server details 
      String serverName = "localhost";
      int serverPort = 4444;
	   System.out.println("Establishing connection. Please wait ...");

      try{
		   socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe){
		  System.out.println("Host unknown: " + uhe.getMessage());
	  }
      catch(IOException ioe){
		  System.out.println("Unexpected exception: " + ioe.getMessage());
	  }
   }

   public void run()
   {
      
	   while (thread != null && purchaseInProg == 0){

         scheduler.scheduleAtFixedRate(() -> {
            try { 
               System.out.println(streamIn.readUTF());
            } catch (Exception e) {
               System.out.println("Error reading from server");
            }
         }, 0, 5, TimeUnit.SECONDS); 

         try {
           String message = console.readLine();
           buyerInterface(message);
         
           }
           catch(IOException ioe)
           {  System.out.println("Sending error: " + ioe.getMessage());
              stop();
           }
        }
   }

   public void buyerInterface(String choice) {
        
         //user input
         if (choice.equals("1")) {
            //Leave market
            System.out.println("Stopping client...");
            stop();  
           
         } else if (choice.equals("2")) {
            purchaseInProg = 1;
            Buy();
           
            
         } else {
            System.out.println("Invalid option @ the interface");
            System.out.println("Welcome to the market Buyer! Would you like to\n1. Leave Market\n2. Buy something\nYou will see a new product for sale every 60 seconds if you would like to buy it please press 2\n");
         }

   }


   public void handle(String msg)
   {  if (msg.equals(".bye"))
      {  System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
      else
         System.out.println(msg);
   }

   public void start() throws IOException
   {
	   console = new BufferedReader(new InputStreamReader(System.in));
      streamOut = new DataOutputStream(socket.getOutputStream());
      streamIn  = new DataInputStream(socket.getInputStream());
      if (thread == null)
      {  
         thread = new Thread(this);
         thread.start();
         System.out.println("Welcome to the market Buyer! Would you like to\n1. Leave Market\n2. Buy something\nYou will see a new product for sale every 60 seconds if you would like to buy it please press 2\n");
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


   public void Buy() {
      purchaseInProg = 1;

      //get user input for what they want to buy
      Scanner scan = new Scanner(System.in);
      String quantity = null;
      String item = null;
      System.out.println("requesting purchase...");
      do {
         System.out.println("Do you want to buy:\n1. Potatoes\n2. Oil\n3. Flour\n4. Sugar");
         item = scan.nextLine();
         if (!(item.equals("1") || item.equals("2") || item.equals("3") || item.equals("4"))) {
            System.out.println("Invalid option");
         }
      } while (!(item.equals("1") || item.equals("2") || item.equals("3") || item.equals("4")));

      do {
          System.out.println("Enter the quantity (in kgs) you would like to purchase i.e. 2");
          quantity = scan.nextLine();
          if (!(quantity.matches("-?\\d+(\\.\\d+)?"))) {
              System.out.println("Not a valid quantity");
          }
      } while (!(quantity.matches("-?\\d+(\\.\\d+)?")));
      
      //broadcast the offer to the server
      String message = item + quantity;
      try {
         streamOut.writeUTF("offer"+message);
         streamOut.flush();
      } catch (IOException ioe) {
         System.out.println("Sending error: " + ioe.getMessage());
         stop();
      }

      //wait to check purchase was successful
      try {
         int rep = 0;
         while (rep == 0) {
            System.out.println("waiting for RESPONSE...");
            String response = streamIn.readUTF();
            System.out.println(response);
            rep = 7;
            break;
            
         }
     } catch (Exception e) {
         // TODO: handle exception
     } finally {
         // Reset purchaseInProg to 0 after completing the purchase
         purchaseInProg = 0;
     }
 }

   public static void main(String args[])
   {  BuyerClient client = null;
      client = new BuyerClient();
   }
}
