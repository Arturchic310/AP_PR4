package PR4_1;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureSample {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Крок 1: Асинхронно згенеровується масив 3x3
        CompletableFuture<int[][]> generateArrayFuture = CompletableFuture.supplyAsync(() -> {
            long taskStart = System.currentTimeMillis();
            int[][] array = new int[3][3];
            Random random = new Random();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    array[i][j] = random.nextInt(100); // Генеруються випадкові числа від 0 до 99
                }
            }
            System.out.println("Масив згенерований за " + (System.currentTimeMillis() - taskStart) + " ms");
            return array;
        });

        // Крок 2: Друкується кожен стовпець асинхронно
        CompletableFuture<Void> printColumnsFuture = generateArrayFuture.thenAcceptAsync(array -> {
            long taskStart = System.currentTimeMillis();
            for (int j = 0; j < 3; j++) {
                StringBuilder column = new StringBuilder("Стовпець " + (j + 1) + ": ");
                for (int i = 0; i < 3; i++) {
                    column.append(array[i][j]).append(", ");
                }
                System.out.println(column.substring(0, column.length() - 2)); // Видалється остання кома та пробіл
            }
            System.out.println("Стовпці надруковані за " + (System.currentTimeMillis() - taskStart) + " ms");
        });

        // Крок 3: Демонстрація RunAsync()
        CompletableFuture.runAsync(() ->  {
            long taskStart = System.currentTimeMillis();
            System.out.println("Випадкове завдання виконано. Випадкова дія виконана за " + (System.currentTimeMillis() - taskStart) + " ms");
        });

        // Крок 4: Демонстрація thenRunAsync()
        CompletableFuture<Void> finalTask = printColumnsFuture.thenRunAsync(() -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Всі завдання виконано. Виконання завершальної дії...");
            System.out.println("Остаточна дія виконана за " + (System.currentTimeMillis() - taskStart) + " ms");
        });

        // Чекаємо виконання всіх завдань
        finalTask.join();
        System.out.println("Загальний час виконання: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
