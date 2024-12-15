package PR4_2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFuturePrototype {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        DecimalFormat df = new DecimalFormat("#.##"); // Формат для чисел з двома знаками після коми

        // Крок 1: Генерація випадкового списку із 20 дійсних чисел
        CompletableFuture<List<Double>> generateNumbers = CompletableFuture.supplyAsync(() -> {
            System.out.println("Генерування випадкових дійсних чисел...");
            List<Double> numbers = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                numbers.add(random.nextDouble() * 200 - 100); // Генеруємо від'ємні та додатні числа від -100 до 100
            }
            return numbers;
        });

        // Крок 2: Розділення чисел на непарні та парні індексовані списки
        CompletableFuture<List<Double>> oddIndexedNumbers = generateNumbers.thenApplyAsync(numbers -> {
            System.out.println("Вилучення чисел з непарними індексами...");
            List<Double> oddIndexed = new ArrayList<>();
            for (int i = 0; i < numbers.size(); i += 2) {
                oddIndexed.add(numbers.get(i));
            }
            return oddIndexed;
        });

        CompletableFuture<List<Double>> evenIndexedNumbers = generateNumbers.thenApplyAsync(numbers -> {
            System.out.println("Вилучення чисел з парними індексами...");
            List<Double> evenIndexed = new ArrayList<>();
            for (int i = 1; i < numbers.size(); i += 2) {
                evenIndexed.add(numbers.get(i));
            }
            return evenIndexed;
        });

        // Крок 3: Знайти min для чисел з непарним індексом і max для чисел з парним індексом
        CompletableFuture<Double> minOdd = oddIndexedNumbers.thenApplyAsync(oddNumbers -> {
            System.out.println("Обчислення максимуму чисел з непарним індексом...");
            return oddNumbers.stream().min(Double::compareTo).orElse(0.0);
        });

        CompletableFuture<Double> maxEven = evenIndexedNumbers.thenApplyAsync(evenNumbers -> {
            System.out.println("Обчислення максимуму чисел з парним індексом...");
            return evenNumbers.stream().max(Double::compareTo).orElse(0.0);
        });

        // Крок 4: Сума minOdd та maxEven
        CompletableFuture<Double> result = minOdd.thenCombineAsync(maxEven, (min, max) -> {
            System.out.println("Результат обчислення для min(odd-indexed) + max(even-indexed)...");
            return min + max;
        });

        // Крок 5: Роздрукуйте згенеровані числа та результат
        result.thenAcceptAsync(finalResult -> {
            try {
                // Відформатований список чисел
                List<Double> generatedNumbers = generateNumbers.get();
                List<String> formattedNumbers = new ArrayList<>();
                for (Double number : generatedNumbers) {
                    formattedNumbers.add(df.format(number));
                }
                System.out.println("Згенеровані числа: " + formattedNumbers);

                // Відображення результату
                System.out.println("Результат (min(odd-indexed) + max(even-indexed)): " + df.format(finalResult));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).join();

        System.out.println("Загальний час виконання: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}

