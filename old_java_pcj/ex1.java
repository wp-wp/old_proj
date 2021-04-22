/**
 *
 * @author wp-wp
 */
import java.util.Random;
import java.io.IOException;
import org.pcj.*;

@RegisterStorage(E1.Shared.class)
public class E1 implements StartPoint {

    @Storage(E1.class)
    enum Shared {
        a, b, c, A
    };

    public double[] b = new double[1048576]; // definiowanie wektora B
    public double[] c = new double[1048576]; // definiowanie wektora C
    public double[] a = new double[1048576];
    public double[] A = new double[1048576];

    public static void main(String[] args) throws IOException {
        PCJ.deploy(E1.class, new NodesDescription("nodes.txt"));
    }

    @Override
    public void main() throws Throwable {

        Random rnd = new Random();
        int n = 1048576; // ilość elementów w wekotrze
        int i;

        //for (i = 0; i < n; i++) { // zapełnianie wektorów B i C randomowymi liczbami z zakresu 0-1 dla wszystkich wątków (inna wersja)
        //    b[i] = 1 * rnd.nextDouble();
        //    c[i] = 1 * rnd.nextDouble();
        //}
        //PCJ.barrier();
        int np = n / PCJ.threadCount();   // ilość elementów sumowanych przez jeden wątek
        int id = PCJ.myId(); // numer wątku
        int ip = id * np;     // początek zakresu sumowania
        int ik = (id + 1) * np;   // koniec zakresu sumowania

        for (i = ip; i < ik; i++) { // zapełnianie wektorów B i C randomowymi liczbami z zakresu 0-1 każdy wątek osobno
            b[i] = 1 * rnd.nextDouble();
            c[i] = 1 * rnd.nextDouble();
            //a[i] = b[i] + c[i];
        }
        PCJ.barrier();

        long start = System.nanoTime(); // początek pomiaru czasu
        for (i = ip; i < ik; i++) {
            a[i] = b[i] + c[i]; // dodawanie wektorów B i C
        }
        PCJ.barrier();
        double czas = ((System.nanoTime() - start) * 1e-9); // zmierzony czas w sekundach
        System.out.println("Czas na wątku " + id + "=  " + czas + "s");
        if (id == 0) {
            if (PCJ.threadCount() == 1) {
                A = a;
            } else {
                A = a;
                for (int v = 1; v < PCJ.threadCount(); v++) {
                    a = PCJ.<double[]>get(v, Shared.a); //zbieranie z innych wątków

                    for (int g = 1; g < n; g++) {
                        A[g] = A[g] + a[g]; //zbieranie z innych wątków
                    }
                }
            }
            System.out.println("A[0] =  " + A[0]);
            System.out.println("A[n-1] =  " + A[n - 1]);
        }
        PCJ.barrier();
    }
}
