package lv.bootcamp.shelter.task5;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 5: Nested test classes for CSV parsing
 * <p>
 * Practice:
 * - @Nested to organize by scenario
 * - @DisplayName for readable output
 * - Testing Optional results (isPresent, isEmpty)
 * - Testing file I/O with temp files
 * <p>
 * Instructions:
 * Write tests for AnimalCsvParser. Use @Nested classes to group tests by scenario.
 * For file-based tests, use Files.createTempFile() and Files.writeString() to create test data.
 */
@DisplayName("AnimalCsvParser")
class AnimalCsvParserTest {

    private AnimalCsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new AnimalCsvParser();
    }

    // ==================== parseRow tests ====================

    @Nested
    @DisplayName("When parsing valid rows")
    class ValidRows {

        @Test
        @DisplayName("parses a complete row into an Animal")
        void shouldParseCompleteRow() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,3,true,2026-01-15");
            assertThat(parseResult).isPresent();
            assertThat(parseResult.get())
                    .extracting(Animal::getName, Animal::getSpecies, Animal::getAge, Animal::isVaccinated, Animal::getIntakeDate)
                    .containsExactly("Buddy","Dog",3,true, LocalDate.of(2026, 01,15));

        }

        @Test
        @DisplayName("trims whitespace from fields")
        void shouldTrimWhitespace() {
            Optional<Animal> parseResult = parser.parseRow("  Buddy , Dog , 3 , true , 2026-01-15 ");
            assertThat(parseResult).isPresent();
            assertEquals("Buddy", parseResult.get().getName());
        }

        @Test
        @DisplayName("parses vaccinated=false correctly")
        void shouldParseFalseVaccination() {
            Optional<Animal> parseResult = parser.parseRow("  Buddy , Dog , 3 , false , 2026-01-15 ");
            assertThat(parseResult).isPresent();
            assertFalse(parseResult.get().isVaccinated());

        }
    }

    @Nested
    @DisplayName("When parsing malformed rows")
    class MalformedRows {

        @Test
        @DisplayName("returns empty for null input")
        void shouldReturnEmptyForNull() {
            Optional<Animal> parseResult = parser.parseRow(null);
            assertThat(parseResult).isEmpty();
        }

        @Test
        @DisplayName("returns empty for blank input")
        void shouldReturnEmptyForBlank() {
            Optional<Animal> parseResult = parser.parseRow("   ");
            assertThat(parseResult).isEmpty();

        }

        @Test
        @DisplayName("returns empty when row has fewer than 5 fields")
        void shouldReturnEmptyForTooFewFields() {
            Optional<Animal> parseResult = parser.parseRow("Buddy, Dog, 3");
            assertThat(parseResult).isEmpty();
        }

        @Test
        @DisplayName("returns empty when name is missing")
        void shouldReturnEmptyForMissingName() {
            Optional<Animal> parseResult = parser.parseRow(",Dog,3,true,2026-01-15");
            assertThat(parseResult).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is not a number")
        void shouldReturnEmptyForBadAge() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,old,true,2026-01-15");
            assertThat(parseResult).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is negative")
        void shouldReturnEmptyForNegativeAge() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,-1,true,2026-01-15");
            assertThat(parseResult).isEmpty();
        }

        @Test
        @DisplayName("returns empty when date is invalid")
        void shouldReturnEmptyForBadDate() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,3,true,not-a-date");
            assertThat(parseResult).isEmpty();
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles vaccinated field as any non-true string → false")
        void shouldTreatNonTrueAsFalse() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,3,maybe,2026-01-15");
            assertThat(parseResult).isPresent();
            assertFalse(parseResult.get().isVaccinated());
        }

        @Test
        @DisplayName("handles age 0 as valid")
        void shouldAcceptAgeZero() {
            Optional<Animal> parseResult = parser.parseRow("Buddy,Dog,0,false,2026-01-15");
            assertThat(parseResult).isPresent();
            assertEquals(0,parseResult.get().getAge());
        }
    }

    // ==================== parseFile tests ====================

    @Nested
    @DisplayName("When parsing a CSV file")
    class ParseFile {

        @Test
        @DisplayName("parses valid rows and counts skipped rows")
        void shouldParseFileAndCountSkipped() throws IOException {
            Path tempFile = Files.createTempFile("test-intake", ".csv");
            String content = """
                    name,species,age,isVaccinated,intakeDate
                    Jerry,Mouse,10,true,2026-03-03
                    Tom,Cat,6,false,2026-01-04
                    Spike,Dog,11,true,2025-10-12
                    Rocky,Dog,2,false,unknown-date
                    """;
            Files.writeString(tempFile,content,StandardCharsets.UTF_8);
            AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);
            assertThat(result.animals()).hasSize(3);
            assertThat(result.skippedRows()).isEqualTo(1);
            Files.deleteIfExists(tempFile);
        }

        @Test
        @DisplayName("returns empty result for file with only a header")
        void shouldReturnEmptyForHeaderOnly() throws IOException {
            Path tempFile = Files.createTempFile("test-intake", ".csv");
            String content = """
                    name,species,age,isVaccinated,intakeDate
                    """;
            Files.writeString(tempFile,content,StandardCharsets.UTF_8);
            AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);
            assertThat(result.animals()).isEmpty();
            assertThat(result.skippedRows()).isZero();
            Files.deleteIfExists(tempFile);
        }

        @Test
        @DisplayName("throws IOException for non-existent file")
        void shouldThrowForMissingFile() {
            assertThrows(IOException.class, () -> parser.parseFile(Path.of("does-not-exist.csv")));
        }
    }
}
