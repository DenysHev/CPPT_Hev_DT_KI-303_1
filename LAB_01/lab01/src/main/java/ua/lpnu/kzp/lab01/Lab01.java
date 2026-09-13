package ua.lpnu.kzp.lab01;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * Консольна програма для обробки даних складу автозапчастин.
 *
 * <p>Формат одного запису:
 * sku;name;stock;unitPrice;supplier</p>
 */
public final class Lab01 {
    private static final int EXPECTED_FIELDS = 5;
    /**
     * Забороняє створення екземплярів службового класу.
     */
    private Lab01() {
    }

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");
        // Обробка аргументів командного рядка.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> {
                    printHelp();
                    return;
                }

                case "--version" -> {
                    System.out.println("lab01 version 1.0.0");
                    return;
                }

                case "--input" -> {
                    if (i + 1 >= args.length) {
                        System.out.println("Помилка: після --input потрібно вказати шлях.");
                        return;
                    }

                    input = Path.of(args[++i]);
                }

                case "--output" -> {
                    if (i + 1 >= args.length) {
                        System.out.println("Помилка: після --output потрібно вказати шлях.");
                        return;
                    }

                    output = Path.of(args[++i]);
                }

                default -> {
                    System.out.println("Невідомий аргумент: " + args[i]);
                    System.out.println("Використайте --help.");
                    return;
                }
            }
        }

        try {
            List<String> lines = Files.readAllLines(input,StandardCharsets.UTF_8);

            ReportResult result = processLines(lines);

            String report = createReport(result);

            System.out.print(report);

            Path outputParent = output.getParent();

            if (outputParent != null) {
                Files.createDirectories(outputParent);
            }
            Files.writeString(output, report, StandardCharsets.UTF_8);

            System.out.println("Звіт записано у: " + output);

        } catch (IOException exception) {
            System.out.println("Помилка роботи з файлом: " + exception.getMessage());
        }
    }

    /**
     * Показує довідку щодо запуску програми.
     */
    private static void printHelp() {
        System.out.println("""
                Використання:
                java -jar lab01.jar [--help] [--version]
                    [--input <файл>] [--output <файл>]

                --help              показати довідку
                --version           показати версію програми
                --input <файл>      шлях до вхідного CSV-файла
                --output <файл>     шлях до файла звіту
                """);
    }

    /**
     * Обробляє всі рядки вхідного файла.
     *
     * @param lines рядки CSV-файла
     * @return результати обробки
     */
    static ReportResult processLines(List<String> lines) {
        int validCount = 0;
        int totalStock = 0;
        double totalInventoryValue = 0.0;
        double maxUnitPrice = 0.0;

        String mostExpensivePart = "";

        List<String> errors = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String line = lines.get(index);

            // Перевірка порожнього рядка.
            if (line.isBlank()) {
                errors.add("Рядок " + lineNumber + ": порожній рядок.");
                continue;
            }

            // Розділення рядка на поля.
            String[] fields = line.split(";", -1);

            // Перевірка кількості полів.
            if (fields.length != EXPECTED_FIELDS) {
                errors.add("Рядок " + lineNumber + ": очікується 5 полів, отримано " + fields.length + ".");
                continue;
            }

            // Перевірка текстових полів.
            if (fields[0].isBlank()
                    || fields[1].isBlank()
                    || fields[4].isBlank()) {

                errors.add("Рядок " + lineNumber + ": SKU, назва або постачальник " + "не можуть бути порожніми.");
                continue;
            }

            try {
                int stock = Integer.parseInt(fields[2]);
                double unitPrice = Double.parseDouble(fields[3]);

                // Перевірка від'ємних значень.
                if (stock < 0 || unitPrice < 0) {
                    errors.add("Рядок " + lineNumber + ": кількість і ціна " + "не можуть бути від'ємними.");
                    continue;
                }

                // Рядок пройшов усі перевірки.
                validCount++;

                totalStock += stock;

                // Вартість запасу = кількість * ціна.
                totalInventoryValue += stock * unitPrice;

                // Пошук найдорожчої деталі.
                if (unitPrice > maxUnitPrice
                        || validCount == 1) {
                    maxUnitPrice = unitPrice;
                    mostExpensivePart = fields[1];
                }

            } catch (NumberFormatException exception) {
                errors.add("Рядок " + lineNumber + ": числове поле має " + "неправильний формат.");
            }
        }

        return new ReportResult(validCount, totalStock, totalInventoryValue, maxUnitPrice, mostExpensivePart, errors);
    }
    /**
     * Формує текстовий звіт.
     *
     * @param result результати обробки
     * @return готовий звіт
     */
    static String createReport(ReportResult result) {
        String lineSeparator = System.lineSeparator();

        StringBuilder report = new StringBuilder();

        report.append("ЗВІТ: СКЛАД АВТОЗАПЧАСТИН").append(lineSeparator);

        report.append("----------------------------------------").append(lineSeparator);

        report.append("Коректних записів: %d%n".formatted(result.validCount()));

        report.append(String.format(Locale.ROOT, "Загальна кількість: %d%n", result.totalStock()));

        report.append(String.format(Locale.ROOT, "Вартість запасу: %.2f%n",result.totalInventoryValue()));

        report.append(String.format(Locale.ROOT, "Найдорожча деталь: %s (%.2f)%n", result.mostExpensivePart(), result.maxUnitPrice()));

        report.append(lineSeparator);
        report.append("Помилки:").append(lineSeparator);

        if (result.errors().isEmpty()) {
            report.append("Немає помилок.").append(lineSeparator);
        } else {
            for (String error : result.errors()) {
                report.append(error).append(lineSeparator);
            }
        }
        return report.toString();
    }
    /**
     * Зберігає результати обробки.
     *
     * @param validCount кількість коректних записів
     * @param totalStock загальна кількість деталей
     * @param totalInventoryValue вартість запасу
     * @param maxUnitPrice максимальна ціна однієї деталі
     * @param mostExpensivePart назва найдорожчої деталі
     * @param errors список помилок
     */
    record ReportResult(
            int validCount,
            int totalStock,
            double totalInventoryValue,
            double maxUnitPrice,
            String mostExpensivePart,
            List<String> errors
    ) {
    }
}