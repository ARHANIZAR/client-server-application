import java.io.*;
import java.net.*;

class ServerTCP {

    ServerSocket conn;
    Socket comm;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    Randomizer rand;
    Authentifier auth;

    public ServerTCP(int serverPort) throws IOException {
        conn = new ServerSocket(serverPort);
        comm = null;
        rand = new Randomizer();
        auth = new Authentifier();
    }

    public void mainLoop() throws IOException,ClassNotFoundException {

        while (true) {

            /* 
               - attendre une connexion, si acceptée récupérer la socket dans comm.
               - instancier oos puis ois grâce à comm.
               - entrer dans le boucle des requêtes
             */
               comm = conn.accept();
               oos = new ObjectOutputStream(comm.getOutputStream());
               ois = new ObjectInputStream(comm.getInputStream());

               try {
                  requestLoop();
               }catch(IOException e){
                System.out.println("io exception : "+e.getMessage());
               }catch(ClassNotFoundException e1){
                System.out.println("class not found : " + e1.getMessage());
               }
        }
    }

    public void requestLoop() throws IOException,ClassNotFoundException {
        int numReq = 0;
        while(true) {

            /* 
               - attendre l'identidiant de la requête
               - en fonction de numReq, appeler la bonne méthode
             */
               numReq = ois.readInt();
               if(numReq == 1)
                  requestGetDouble();
                else if(numReq == 2)
                  requestGetShuffle();
                else if (numReq == 3) 
                  requestSetSeed();
                else if(numReq == 4)
                  requestGetAleaFile();
                else
                  break;
        }
    }

    private void requestGetDouble() throws IOException,ClassNotFoundException {
        System.out.println("received a GETDOUBLE request");
        /* 
           - utiliser rand pour générer un double
           - envoyer au client ce double.
         */
        double result = rand.getDouble();
        oos.writeDouble(result);
        oos.flush();
    }

    private void requestGetShuffle() throws IOException,ClassNotFoundException {
        System.out.println("received a SHUFFLE request");
        /* 
           - recevoir la String représentant la chaîne à mélanger + le nombre de permutation
           - si ce nombre est < 0 ou > 100, renvoyer false et temriner la méthode
           - sinon :
              - renvoyer true
              - utiliser rand pour mélanger la String
              - envoyer au client le résultat.
         */

        String chaine = (String)ois.readObject();
        int nb = ois.readInt();
        if(nb < 0 || nb > 100){
          oos.writeBoolean(false);
        }else {
          oos.writeBoolean(true);
          String result = rand.shuffle(chaine, nb);
          oos.writeObject(result);
        }

        oos.flush();
        

    }

    private void requestSetSeed() throws IOException,ClassNotFoundException {
        System.out.println("received a SETSEED request");

        /* 
           - recevoir le login et mot de passe
           - utiliser auth pour vérifier s'ils correspondent.
           - envoyer au client -1, -2 ou 0 selon l'erreur/réussite
           - si réussite, recevoir la graine et mettre à jour rand
         */

           long login = ois.readLong();
           String pass = (String) ois.readObject();
           int rep = auth.authentify(login,pass);
           oos.writeInt(rep);
           oos.flush();
           if(rep == 0){
            long grain = ois.readLong();
            rand.setSeed(grain);
           }
    }


    private void requestGetAleaFile() throws IOException,ClassNotFoundException {
        System.out.println("received a ALEAFILE request");

        /* A
           - recevoir la taille du fichier à générer.
           - si taille <=0 ou > 100000, envoyer -1 au client et sortir de la méthode
           - sinon envoyer au client cette même taille.
           - créer un buffer de 512 octets.
           - remplir ce buffer autant de fois que nécessaire grâce à rand et l'envoyer au client.
           NB 1 : le nombre de fois dépend de la taille demandée, sachant que celle-ci ne se divise
           par forcément par 512. Par exemple, si la taille demandée est 1030, alors il faudra
            remplir 3 fois le buffer (1030/512 = 2.011...) et faire 3 envois
           NB 2 : attention à ne pas envoyer trop d'octets lors du dernier envoi. Par exemple,
            avec 1030 octets à envoyer, les deux premiers envois font bien 512 octets, mais le
            dernier fera 1030-2*512 = 6 octets.
         */
        long size = ois.readLong();

        if(size <= 0 || size > 100000){
          oos.writeLong(-1);
        }else {
          oos.writeLong(size);
          byte[] buf = new byte[512];
          long taillePaquet = 512L;
          long count = size/taillePaquet;

          for(int i = 0; i<count; i++){
            rand.fillBuffer(buf);
            oos.write(buf);
            oos.flush();

          }
          long reste = size-count*taillePaquet;
          String restString = ""+reste;
          buf = new byte[Integer.parseInt(restString)];
          rand.fillBuffer(buf);
          oos.write(buf);
          oos.flush();

        }
    }
}
