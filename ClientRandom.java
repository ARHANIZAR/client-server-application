import java.net.*;
import java.io.*;
 
class ClientRandom {
 
  public static void usage() {
    System.out.println("usage : ClientRandom serveur port");
    System.exit(1);
  }
 
  public static void main(String[] args) {
 
    if (args.length != 2) {
      usage();
    }
    int port = Integer.parseInt(args[1]);
    ClientTCP client = null;
    try {
      client = new ClientTCP(args[0], port);
      client.requestLoop();
    }
    catch(IOException e) {
      System.err.println("cannot communicate with server");
      System.exit(1);
    }
    catch(ClassNotFoundException e) {
      System.err.println("cannot communicate with server");
      System.exit(1);
    }
  }
}