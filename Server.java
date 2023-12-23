import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;  //to broadcast new client to already existing clients
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    public Server() {
        done=false;
        connections=new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            server= new ServerSocket(9999);
            pool=Executors.newCachedThreadPool();
            //to always accept connection we used done.
            while(!done) {
                Socket client=server.accept();
                ConnectionHandler handler=new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }

        }catch(IOException e) {
            shutdown();
        }

    }

//    public void broadcast(String message) {
//        for(ConnectionHandler ch: connections) {
//            if(ch!=null) {
//                ch.sendMesssage(message);
//            }
//        }
//    }

    public void shutdown() {
        try {
            done=true;
            if(!server.isClosed()) {
                server.close();
            }
            for(ConnectionHandler ch: connections) {
                ch.shutdown();
            }
        }catch(IOException e) {
            // ignore
        }
    }

//	 class ConnectionHandler implements Runnable{
//		private Socket client;
//		private BufferedReader in;  //reads information from client
//		private PrintWriter out;    //writes information to the client
//		private String nickname;
//		public ConnectionHandler(Socket client) {
//			this.client=client;
//		}
//
//		@Override
//		public void run() {
//			try {
//				out=new PrintWriter(client.getOutputStream());
//				out.flush();
//				in=new BufferedReader(new InputStreamReader(client.getInputStream()));
//				out.println("Please enter a nickname:");
//				nickname=in.readLine();
//				System.out.println(nickname+"connected");
//				broadcast(nickname+"joined the chat!");  //broadcast to all the client about new client who joined the chat
//				String message;
//				while((message = in.readLine())!= null) {
//					if(message.startsWith("/nick ")) {
//
//						String[] messageSplit=message.split(" ", 2);
//
//						if(messageSplit.length==2) {
//							broadcast(nickname+ "renamed themselves to" + messageSplit[1]);
//							System.out.println(nickname+ "renamed themselves to" + messageSplit[1]);
//							nickname=messageSplit[1];
//							out.println("Successfully changed nickname to"+ nickname);
//						}
//
//						else {
//							out.println("No nickname provided");
//						}
//
//					}
//
//					else if(message.startsWith("/quit")) {
//						broadcast(nickname + "left the chat.");
//						shutdown();
//					}
//					else {
//						broadcast(nickname+": " + message);
//					}
//				}
//			}catch(IOException e) {
//				shutdown();
//			}
//		}
//
//		public void sendMesssage(String message) {
//			out.println(message);
//		}
//
//		public void shutdown() {
//			try {
//				in.close();
//				out.close();
//				if(!client.isClosed()) {
//					client.close();
//				}
//			} catch(IOException e) {
//				//ignore
//			}
//		}
//
//
//
//}
public static void main(String[] args) {
    Server server=new Server();
    server.run();
}


}

