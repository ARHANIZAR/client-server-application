import java.util.*;

class Randomizer {

	private static Random loto = new Random(Calendar.getInstance().getTimeInMillis());

	public Randomizer() {
	}

	/* setSeed() :
       met à jour la graine du générateur aléatoire
     */
	public void setSeed(long seed) {
		loto.setSeed(seed);
	}

	/* shuffle() :
       mélange les caractères de input en faisant nbPermut
       permutations aléatoire de caractères.
     */
	public String shuffle(String input, int nbPermut) {
		char[] buf = input.toCharArray();
		int l1,l2;
		char tmp;
		for(int i=0;i<nbPermut;i++) {
			l1 = loto.nextInt(input.length());
			l2 = loto.nextInt(input.length());
			if (l1 != l2) {
				tmp = buf[l1];
				buf[l1] = buf[l2];
				buf[l2] = tmp;
			}
		}
		return new String(buf);
	}

	/* getDouble() :
	   retourne un double tiré aléatoirement.
	 */
	public double getDouble() {
		return loto.nextDouble();
	}

	/* fillBuffer() :
	   rempli buf avec des octets tirés aléatoirement.
	 */
	public void fillBuffer(byte[] buf) {
		loto.nextBytes(buf);
	}
}
