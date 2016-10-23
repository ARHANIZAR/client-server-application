import java.io.*;
import java.util.*;

class Authentifier {

    private Map<Long,String> passwords;

    public Authentifier() {
        passwords = new HashMap<Long,String>();
        initPasswords();
    }

    private void initPasswords() {

        passwords.put(new Long(1234567890L),"azertyui");
        passwords.put(new Long(9876543210L),"qsdfghjk");
    }

    /* authentify() :
       vérifier si le couple login, pass est présent dans le tableau
       associatif des mots de passes passwords.
       retourne :
          -1 : si login inexsitant
          -2 : si login ok mais mot de passe invalide
          0 : sinon
     */
    public int authentify(long login, String pass) {
        if (! passwords.containsKey(login)) return -1;

        if (! pass.equals(passwords.get(login))) {
            return -2;
        }
        return 0;
    }
}
