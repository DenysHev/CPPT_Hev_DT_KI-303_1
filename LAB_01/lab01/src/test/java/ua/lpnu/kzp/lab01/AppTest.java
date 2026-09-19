package ua.lpnu.kzp.lab01;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Lab01 application.
 */
class AppTest {

    @Test
    void testBlankLine() {
        List<String> lines = List.of("");

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors().get(0).contains("порожній рядок"));
    }
    /**
     * Checks a correct record.
     */
    @Test
    void testCorrectRecord() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Bosch"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(1, result.validCount());
        assertEquals(25, result.totalStock());
        assertEquals(21262.50, result.totalInventoryValue(), 0.0001);
        assertEquals(850.50, result.maxUnitPrice(), 0.0001);
        assertEquals("Гальмівні колодки", result.mostExpensivePart());
        assertTrue(result.errors().isEmpty());
    }

    /**
     * Checks a non-numeric stock value.
     */
    @Test
    void testNonNumericStock() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;abc;850.50;Bosch"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(
                result.errors().get(0).contains("числове поле")
        );
    }

    /**
     * Checks a negative stock value.
     */
    @Test
    void testNegativeStock() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;-5;850.50;Bosch"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(
                result.errors().get(0).contains("не можуть бути від'ємними")
        );
    }

    /**
     * Checks an incorrect number of fields.
     */
    @Test
    void testIncorrectNumberOfFields() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(
                result.errors().get(0).contains("очікується 5 полів")
        );
    }

    /**
     * Checks an empty required text field.
     */
    @Test
    void testEmptyName() {
        List<String> lines = List.of(
                "SKU001;;25;850.50;Bosch"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(
                result.errors().get(0).contains("не можуть бути порожніми")
        );
    }

    /**
     * Checks total stock calculation.
     */
    @Test
    void testTotalStock() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Bosch",
                "SKU002;Масляний фільтр;40;320.00;Mann",
                "SKU003;Свічки запалювання;60;450.75;NGK"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(3, result.validCount());
        assertEquals(125, result.totalStock());
    }

    /**
     * Checks total inventory value calculation.
     */
    @Test
    void testInventoryValue() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Bosch",
                "SKU002;Масляний фільтр;40;320.00;Mann"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        double expectedValue = 25 * 850.50 + 40 * 320.00;

        assertEquals(
                expectedValue,
                result.totalInventoryValue(),
                0.0001
        );
    }

    /**
     * Checks the most expensive part.
     */
    @Test
    void testMostExpensivePart() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Bosch",
                "SKU002;Акумулятор;10;4200.00;Varta",
                "SKU003;Масляний фільтр;40;320.00;Mann"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(4200.00, result.maxUnitPrice(), 0.0001);
        assertEquals("Акумулятор", result.mostExpensivePart());
    }

    /**
     * Checks that invalid records do not affect statistics.
     */
    @Test
    void testInvalidRecordsAreIgnored() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Bosch",
                "SKU002;Неправильний запис;abc;320.00;Mann",
                "SKU003;Масляний фільтр;-10;320.00;Mann"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(1, result.validCount());
        assertEquals(25, result.totalStock());
        assertEquals(21262.50, result.totalInventoryValue(), 0.0001);
    }

    /**
     * Checks processing of an empty input.
     */
    @Test
    void testEmptyInput() {
        List<String> lines = List.of();

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(0, result.validCount());
        assertEquals(0, result.totalStock());
        assertEquals(0.0, result.totalInventoryValue(), 0.0001);
        assertTrue(result.errors().isEmpty());
    }

    /**
     * Checks Ukrainian characters in input data.
     */
    @Test
    void testUkrainianCharacters() {
        List<String> lines = List.of(
                "SKU001;Гальмівні колодки;25;850.50;Бош"
        );

        Lab01.ReportResult result = Lab01.processLines(lines);

        assertEquals(1, result.validCount());
        assertEquals("Гальмівні колодки", result.mostExpensivePart());
    }
}