import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class SaleThread extends Thread {

    private Socket socket   = null;
    private SellerClient salesman    = null;
    private Thread thread              = null;
    private DataOutputStream streamOut = null;
    private DataInputStream streamIn = null;

    String serverName = "localhost";
    int serverPort = 4444;

    public SaleThread(SellerClient _client, Socket _socket) {

        salesman = _client;
        socket   = _socket;
        thread = new Thread(this); 
        try {
            streamOut = new DataOutputStream(socket.getOutputStream());
            streamIn  = new DataInputStream(socket.getInputStream());
            thread.start();
        } catch (Exception e) {
            System.out.println("Unable to make purchase" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        
    }

    public synchronized void run() {
        while (thread != null){
            try {
                String offer = streamIn.readUTF();
                //get ID of buyer
                int num = offer.indexOf(":");
                int ID = Integer.parseInt(offer.substring(0, num));

                int index = Integer.parseInt(offer.substring(offer.length()-2, offer.length()-1));
                int quantity = Integer.parseInt(offer.substring(offer.length()-1));
                if(offer.contains("offer")){
                    salesman.messageFromSale(ID, index-1, quantity);
                    
                }
                
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    
}
