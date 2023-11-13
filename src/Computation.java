import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Computation {
    final static int CASE1_ATTEMPTS = 3;
    static int attempt = CASE1_ATTEMPTS;

    static int nonCriticalErrorsfx;

    static int nonCriticalErrorsgx;
    static Random random = new Random();

    public static Optional<Double> compfunc(int x_value, int n) {
        switch (n) {
            case 0:
                while (attempt > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        attempt--;
                        if (random.nextDouble() < 0.3) {
                            nonCriticalErrorsfx++;
                            System.err.println("Некритична помилка в обчисленнях f(x).");
                        }
                        // Повертаємо правильне значення f(x)
                        return Optional.of(x_value + 10.00);
                    } catch (InterruptedException ie) {
                    }
                    if(attempt <= 0)
                        return Optional.empty();
                }
                return Optional.empty();

            case 1:
                while (attempt > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        attempt--;
                        if (random.nextDouble() < 0.5) {
                            nonCriticalErrorsgx++;
                            System.err.println("Некритична помилка в обчисленнях g(x).");
                        }
                        // Повертаємо правильне значення f(x)
                        else return Optional.of(x_value + 7.00);
                    } catch (InterruptedException ie) {
                    }
                    if(attempt <= 0)
                        return Optional.empty();
                }
                return Optional.empty();


            default:
                // Обробка інших варіантів
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException ie) {
                }
                return Optional.empty();
        }
    }
    public static int getNonCriticalErrorsfx() {
        return nonCriticalErrorsfx;
    }
    public static int getNonCriticalErrorsgx() {
        return nonCriticalErrorsgx;
    }
}