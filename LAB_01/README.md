# Лабораторна робота №1 — Склад автозапчастин

**Курс:** Кросплатформні засоби програмування  
**Варіант:** 4  
**Предметна область:** Склад автозапчастин  
**Java:** 21  
**Maven artifact:** `ua.lpnu.kzp:lab01:1.0.0`

## Призначення програми

Консольна Java-програма читає записи про автозапчастини з CSV-файла, перевіряє коректність даних, пропускає помилкові записи із повідомленням про номер рядка та причину, обчислює чотири показники та формує один текстовий звіт у консоль і файл.

Для варіанта 4 формат одного запису та показники задані методичними вказівками:

```text
sku:String; name:String; stock:int; unitPrice:double; supplier:String
```

Показники:

1. кількість коректних записів;
2. загальна кількість;
3. вартість запасу;
4. найдорожча деталь.

## Формат вхідного файла

Вхідний файл за замовчуванням:

```text
data/input.csv
```

Кодування — UTF-8. Поля одного запису розділяються символом `;`.

У CSV типи полів не записуються.

Приклад:

```text
SKU001;Гальмівні колодки;25;850.50;Bosch
SKU002;Масляний фільтр;40;320.00;Mann
SKU003;Свічки запалювання;60;450.75;NGK
SKU004;Акумулятор;10;4200.00;Varta
SKU005;Повітряний фільтр;abc;280.00;Bosch
SKU006;Гальмівний диск;-5;1800.00;Brembo
```

Перші чотири записи є коректними, а останні два містять помилки.

## Перевірка даних

Програма перевіряє:

- порожній рядок;
- правильну кількість полів;
- непорожні `sku`, `name` та `supplier`;
- числовий формат `stock` і `unitPrice`;
- недопустимі від'ємні значення.

Помилка одного рядка не завершує програму. У повідомленні вказуються номер рядка та причина пропуску.

## Обчислення

Для кожного коректного запису:

- `totalStock` — сума значень `stock`;
- `totalInventoryValue` — сума `stock * unitPrice`;
- `maxUnitPrice` — найбільша ціна одиниці;
- `mostExpensivePart` — назва деталі з найбільшою ціною.

Некоректні записи до статистики не включаються.

## Структура проєкту

```text
lab01/
├── .github/
│   └── workflows/
│       └── ci.yml
├── .mvn/
│   └── wrapper/
├── data/
│   └── input.csv
├── out/
│   └── report.txt
├── src/
│   ├── main/java/ua/lpnu/kzp/lab01/
│   │   └── Lab01.java
│   └── test/java/ua/lpnu/kzp/lab01/
│       └── AppTest.java
├── ai/
│   ├── manager.md
│   └── devops.md
├── .editorconfig
├── .gitattributes
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── REPORT.md
```

## Вимоги

- JDK 21;
- Maven Wrapper;
- Git;
- GitHub для репозиторію та CI.

## Запуск через Maven Wrapper

### macOS / Linux

```bash
./mvnw clean
./mvnw test
./mvnw verify
./mvnw package
```

### Windows

```powershell
mvnw.cmd clean
mvnw.cmd test
mvnw.cmd verify
mvnw.cmd package
```

## Запуск JAR

Після `package`:

```bash
java -jar target/lab01-1.0.0.jar
```

Довідка:

```bash
java -jar target/lab01-1.0.0.jar --help
```

Версія:

```bash
java -jar target/lab01-1.0.0.jar --version
```

Власний вхідний файл:

```bash
java -jar target/lab01-1.0.0.jar --input data/input.csv
```

Власний файл звіту:

```bash
java -jar target/lab01-1.0.0.jar --input data/input.csv --output out/report.txt
```

## Приклад результату

Для наведеного вище `input.csv` очікується:

```text
ЗВІТ: СКЛАД АВТОЗАПЧАСТИН
----------------------------------------
Коректних записів: 4
Загальна кількість: 135
Вартість запасу: 103107.50
Найдорожча деталь: Акумулятор (4200.00)

Помилки:
Рядок 5: числове поле має неправильний формат.
Рядок 6: кількість і ціна не можуть бути від'ємними.
```

Звіт також записується у `out/report.txt`.

## Якість і CI

Проєкт використовує:

- Maven;
- JUnit 5;
- SpotBugs у фазі `verify`;
- Maven Shade Plugin для створення виконуваного JAR;
- GitHub Actions для перевірки на `ubuntu-latest`, `windows-latest` та `macos-latest`.

Workflow використовує Java 21 та Maven Wrapper.

## Репозиторій

GitHub: https://github.com/DenysHev/CPPT_Hev_DT_KI-303_1

