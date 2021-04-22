
/**
 *
 * @author wp-wp
 */
import java.util.Random;
import java.io.IOException;
import org.pcj.*;

@RegisterStorage(E2.Shared.class)
public class E2 implements StartPoint {

    @Storage(E2.class)
    enum Shared {
        a, norma, kwadrat, suma
    };
    public double[] a = new double[1024];
    public double norma = 0.0;
    public double suma = 0.0;
    public double[] kwadrat = new double[1024];

    public static void main(String[] args) throws IOException {
        PCJ.deploy(E2.class, new NodesDescription("nodes2.txt"));
    }

    @Override
    public void main() throws Throwable {

        Random rnd = new Random();
        int n = 1024; // ilość elementów w wekotrze
        int i;
        int np = n / PCJ.threadCount(); // ilość elementów sumowanych przez jeden wątek
        int id = PCJ.myId(); // numer wątku
        int ip = id * np;  // początek zakresu sumowania
        int ik = (id + 1) * np; // koniec zakresu sumowania

        for (i = ip; i < ik; i++) {  // zapełnianie wektora A randomowymi liczbami z zakresu 0-1
            a[i] = 1 * rnd.nextDouble();
        }
        PCJ.barrier();

        long start = System.nanoTime(); // początek pomiaru czasu
        for (i = ip; i < ik; i++) {
            kwadrat[i] += (double) Math.pow(a[i], 2); //a[i]*a[i]; podnoszenie do kwadratu el. wektora
            suma += kwadrat[i];

        }
        //System.out.println("suma =  " + suma);
        PCJ.barrier();

        if (PCJ.myId() == 0) {
            if (PCJ.threadCount() == 1) {
                double sum = suma;
                norma = (double) Math.sqrt(sum);
            } else {

                double sum = suma;
                for (int v = 1; v < PCJ.threadCount(); v++) {
                    suma = PCJ.get(v, Shared.suma);

                    for (int l = 1; l < PCJ.threadCount(); l++) {
                        suma = sum + suma;
                        norma = (double) Math.sqrt(suma);
                    }
                }
            }
            System.out.println("norma wektora A[i] =  " + norma);
        }
        PCJ.barrier();
        double czas = ((System.nanoTime() - start) * 1e-9); // zmierzony czas w sekundach
        System.out.println("Czas na wątku " + id + "=  " + czas + "s");
    }
}
