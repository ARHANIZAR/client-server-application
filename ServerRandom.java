import java.io.*;
 
class ServerRandom {
 
  public static void usage() {
    System.out.println("usage : ServerRandom port");
    System.exit(1);
  }
 
  public static void main(String[] args) {
 
    if (args.length != 1) {
      usage();
    }
    int port = Integer.parseInt(args[0]);
    ServerTCP server = null;
    try {
      server = new ServerTCP(port);
      server.mainLoop();
    }
    catch(IOException e) {
      System.err.println("cannot communicate with client");
      System.exit(1);
    }
    catch(ClassNotFoundException e) {
      System.err.println("cannot communicate with client");
      System.exit(1);
    }
  }
}