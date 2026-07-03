package lv.bootcamp.shelter.stretch;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Stretch goal: Testing file output
 *
 * Practice:
 * - Writing to temp files and reading them back
 * - String content assertions
 * - Cleanup with Files.deleteIfExists
 *
 * Instructions:
 * These tests verify that AnimalReportWriter produces correct output.
 * This task is optional — attempt it after completing tasks 1–6.
 */
@DisplayName("AnimalReportWriter (stretch)")
class AnimalReportWriterTest {

    private final AnimalReportWriter writer = new AnimalReportWriter();

    @Test
    @DisplayName("writes report file that contains total count")
    void shouldWriteTotalCount() throws IOException {
        List<Animal> animals = List.of(
                new Animal("Tom", "Cat",3,false,LocalDate.of(2026,11,11)),
                new Animal("Jerry", "Mouse",5,false,LocalDate.of(2026,11,11)),
                new Animal("Spike", "Dog",6,false,LocalDate.of(2026,11,11)));

        Path output = Files.createTempFile("report-test", ".txt");
        writer.writeReport(animals,output);
        String content = Files.readString(output,StandardCharsets.UTF_8);
        assertThat(content).contains("Total animals: 3");
        Files.deleteIfExists(output);
    }

    @Test
    @DisplayName("writes per-species breakdown in alphabetical order")
    void shouldWriteSpeciesBreakdown() throws IOException {
        // TODO: Read content and verify "Cat:" appears before "Dog:" (alphabetical)
        List<Animal> animalList = new ArrayList<>();
        Animal tom = new Animal("Tom", "Cat", 3, true, LocalDate.of(2026, 11, 11));
        Animal spike = new Animal("Spike", "Dog", 4, true, LocalDate.of(2026, 11, 11));
        animalList.add(tom);
        animalList.add(spike);
        Path tempFile = Files.createTempFile("report-test", ".txt");
        writer.writeReport(animalList, tempFile);
        String content = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertThat(content)
                .contains("Cat: 1")
                .contains("Dog: 1");
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("writes oldest animal per species")
    void shouldWriteOldestPerSpecies() throws IOException {
        List<Animal> animalList = new ArrayList<>();
        Animal tom = new Animal("Tom", "Cat", 3, true, LocalDate.of(2026, 11, 11));
        Animal spike = new Animal("Spike", "Dog", 4, true, LocalDate.of(2026, 11, 11));
        Animal max = new Animal("Max","Dog",5,true,LocalDate.of(2022,11,11));
        animalList.add(tom);
        animalList.add(spike);
        animalList.add(max);
        Path tempFile = Files.createTempFile("report-test", ".txt");
        writer.writeReport(animalList, tempFile);
        String content = Files.readString(tempFile,StandardCharsets.UTF_8);
        assertThat(content).contains("Dog: Max (age 5)");
        Files.deleteIfExists(tempFile);
    }
}
