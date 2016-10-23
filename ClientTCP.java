import java.io.*;
import java.net.*;
import java.util.*;

class ClientTCP  {

	Socket comm;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	public ClientTCP(String serverIp, int serverPort) throws IOException {

		/* 
		   - instanciation de comm se connectant à serverIp/serverPort
		   - création des flux objet ois puis oos grâce à comm
		 */
		comm = new Socket(serverIp, serverPort);
		ois = new ObjectInputStream(comm.getInputStream());
		oos = new ObjectOutputStream(comm.getOutputStream());  
	}

	public void requestLoop() throws IOException,ClassNotFoundException {

		String reqLine = null;
		BufferedReader consoleIn = null;
		String[] reqParts = null;

		consoleIn = new BufferedReader(new InputStreamReader(System.in));
		boolean stop = false;

		while (!stop) {

			System.out.print("Client> ");
			reqLine = consoleIn.readLine();
			reqParts = reqLine.split(" ");

			if (reqParts[0].equals("GETDOUBLE")) {
				// envoi de l'identifiant entier de la requete (c.a.d 1)
				oos.writeInt(1);
				oos.flush();
				// lecture de la réponse
				double d = ois.readDouble();
				// affichage de la réponse
				System.out.println("valeur reçue : "+d);
			}
			else if (reqParts[0].equals("SHUFFLE")) {
				// déclaration des paramètres de la requête
				String str;
				int nbPerm;
				// initialisation des paramètres de la requête
				str = reqParts[1];
				nbPerm = Integer.parseInt(reqParts[2]);

				boolean rep = false; // la réponse du serveur
				/* 
				   - envoi de l'identifiant entier de la requete (c.a.d 2)
				   - envoi de str et nbPerm.
				   - récéption de la réponse -> rep
				   - si rep est faux : affichage message erreur sur le nb de permutation
				   - sinon recevoir la String envoyée par le serveur et l'afficher
				 */
				oos.writeInt(2);
				oos.writeObject(str);
				oos.writeInt(nbPerm);
				oos.flush();
				rep = ois.readBoolean();
				if(rep == false){
					System.out.println("err : 0 < nombre de permutation < 100 ");
				}else{
					String result = (String)ois.readObject();
					System.out.println(result);
				}

			}
			else if (reqParts[0].equals("SETSEED")) {
				// déclaration des paramètres de la requête
				long login; // le login
				String pass; // le mdp
				long seed; // la graine

				int rep; // la réponse du serveur.

				/*
				   - initialiser les paramètres de la requête
				   - envoi de l'identifiant entier de la requete (c.a.d 3)
				   - envoi de login et pass
				   - réception de la réponse -> rep
				   - si rep == -2 : affichage message erreur sur le mdp
				   - sinon si rep == -1 : affichage message erreur sur le login
				   - sinon envoi de seed
				 */

				login = Long.parseLong(reqParts[1]);
				pass = reqParts[2];
				seed = Long.parseLong(reqParts[3]);

				oos.writeInt(3);
				oos.writeLong(login);
				oos.writeObject(pass);
				oos.flush();

				rep = ois.readInt();
				if (rep == -1){
					System.out.println("login incorecte !");
				}else if( rep == -2){
					System.out.println("pass incorecte !");
				}else if(rep == 0){
					oos.writeLong(seed);
					System.out.println("Ok!");
				}

			}
			else if (reqParts[0].equals("ALEAFILE")) {
				// déclaration des paramètres de la requête
				long size; // la taille du fichier à générer
				String fileName; // le nom du fichier pour stocker les données reçues.

				long rep; // la réponse du serveur (soit < 0, soit égal à size)
				int nbLu; // pour compter les octets reçu à chaque lecture
				long total = 0; // pour compter le total reçu
				byte[] buf = null; // pour recevoir les octets

				/* 
				   - initialiser les paramètres de la requête
				   - envoi de l'identifiant entier de la requete (c.a.d 4)
				   - envoi de size
				   - réception de la réponse -> rep
				   - si rep < 0 : affichage message erreur sur la taille du fichier
				   - sinon :
				      - créer un flux d'octet sortant vers fichier fileName
				      - créer buf avec comme taille 1024 octets
				      - recevoir les octets en provenance du serveur et les sotcker dans le fichier jusqu'à en avoir reçu rep.
				 */
				size = Long.parseLong(reqParts[1]);
				fileName = reqParts[2];

				oos.writeInt(4);
				oos.writeLong(size);
				oos.flush();

				rep = ois.readLong();
				if(rep < 0){
					System.out.println("Err : 0 < taille du fichier < 100000");
				}else{
					//File file = new File(fileName);
					//BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
					FileOutputStream fos = new FileOutputStream(fileName);
					buf = new byte[512];
					int nbPaquet = Integer.parseInt(""+rep/512L);
					//nbPaquet = (rep%512L > 0) ? nbPaquet + 1 : nbPaquet;
					for(int i = 0; i < nbPaquet; i++){
						ois.read(buf,0,512);
						fos.write(buf);
						//bw.write(buf,0,Integer.parseInt(""+rep));
					}
					int lastBytes = Integer.parseInt(""+(rep - nbPaquet * 512));
					if(rep%512L > 0){
						buf = new byte[lastBytes];
						ois.read(buf,0,lastBytes);
						fos.write(buf);
					}

				}
			}
			else if (reqParts[0].equals("QUIT")) {
				stop = true;
			}
		}
	}
}
