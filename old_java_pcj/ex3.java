/**
 *
 * @author wp-wp
 */
import java.io.*;
import org.pcj.*;
import java.util.*;

@RegisterStorage(E3.Shared.class)
public class E3 implements StartPoint {

    @Storage(E3.class)
    enum Shared {
        B, b, wektorB
    };
    public int B[] = new int[100];
    public int b[] = new int[100];
    public int[] wektorB = new int[100];

    public static void main(String[] args) throws IOException {
        PCJ.deploy(E3.class, new NodesDescription("nodes3.txt"));
    }

    @Override
    public void main() throws Throwable {
        BufferedReader wczytywanie = new BufferedReader(new FileReader("in.txt"));
        String nextLine = null;

        int n = 100;
        int i;
        int np = n / PCJ.threadCount();
        int id = PCJ.myId();
        int ip = id * np;
        int ik = (id + 1) * np;
        long start = System.nanoTime();

        for (i = ip; i < ik; i++) {
            while ((nextLine = wczytywanie.readLine()) != null) {
                int liczba = Integer.parseInt(nextLine);
                b[liczba] = b[liczba] + 1;
            }
            B[i] = b[i];
        }
        PCJ.barrier();
        double czas = ((System.nanoTime() - start) * 1e-9);
        System.out.println("Czas na wątku " + id + "=  " + czas + "s");

        if (id == 0) {
            if (PCJ.threadCount() == 1) {
                wektorB = B;
            } else {
                wektorB = B;
                for (int v = 1; v < PCJ.threadCount(); v++) {
                    B = PCJ.get(v, Shared.B);

                    for (int g = 0; g < n; g++) {
                        wektorB[g] = wektorB[g] + B[g];
                    }
                }
            }
            System.out.println("B =  " + Arrays.toString(wektorB));
        }
        PCJ.barrier();
    }
}
