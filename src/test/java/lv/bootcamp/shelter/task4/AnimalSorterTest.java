package lv.bootcamp.shelter.task4;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task 4: Collection and sorting tests
 *
 * Practice:
 * - AssertJ list assertions (extracting, containsExactly)
 * - Testing sort order
 * - Testing null/empty input handling
 *
 * Instructions:
 * Write tests for AnimalSorter. Use AssertJ's extracting() and containsExactly()
 * to verify the order of results.
 */
@DisplayName("AnimalSorter")
class AnimalSorterTest {

    private AnimalSorter sorter;

    private Animal buddy;
    private Animal luna;
    private Animal max;
    private Animal bella;

    @BeforeEach
    void setUp() {
        sorter = new AnimalSorter();
        buddy = new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15));
        luna = new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 10));
        max = new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 20));
        bella = new Animal("Bella", "Cat", 1, true, LocalDate.of(2026, 1, 5));
    }

    // --- sortByAge ---

    @Test
    @DisplayName("sortByAge: returns animals ordered youngest to oldest")
    void shouldSortByAgeAscending() {
        List<Animal> animalList = List.of(buddy,luna,max,bella);
        List<Animal> result = sorter.sortByAge(animalList);

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Bella", "Luna", "Buddy", "Max");
    }

    @Test
    @DisplayName("sortByAge: returns empty list for null input")
    void shouldReturnEmptyForNullInput() {
        assertThat(sorter.sortByAge(null)).isEmpty();
    }

    @Test
    @DisplayName("sortByAge: returns empty list for empty input")
    void shouldReturnEmptyForEmptyInput() {
        assertThat(sorter.sortByAge(List.of())).isEmpty();
    }

    @Test
    @DisplayName("sortByAge: does not modify the original list")
    void shouldNotModifyOriginalList() {
        List<Animal> animalList = new ArrayList<>(List.of(buddy, luna, max, bella));
        sorter.sortByAge(animalList);
        assertThat(animalList).containsExactly(buddy, luna, max, bella);
    }
    // --- sortByName ---

    @Test
    @DisplayName("sortByName: returns animals in alphabetical order")
    void shouldSortByNameAlphabetically() {
            assertThat(sorter.sortByName(List.of(buddy, luna, max, bella)))
                    .extracting(Animal::getName)
                    .containsExactly("Bella", "Buddy", "Luna", "Max");
    }

    @Test
    @DisplayName("sortByName: is case-insensitive")
    void shouldSortNamesCaseInsensitively() {
        Animal zebra;
        Animal alpha;
        Animal loki;
        zebra = new Animal("zebra", "Cat", 15,false,LocalDate.of(2026, 1, 25));
        alpha = new Animal("Alpha", "Dog", 7,true,LocalDate.of(2026, 1, 13));
        loki = new Animal("loki", "Cat", 4,false,LocalDate.of(2026, 1, 22));
        assertThat(sorter.sortByName(List.of(buddy, luna, max, bella, zebra,alpha,loki)))
                .extracting(Animal::getName)
                .containsExactly("Alpha", "Bella", "Buddy", "loki", "Luna", "Max", "zebra");
    }

    // --- sortByIntakeDate ---

    @Test
    @DisplayName("sortByIntakeDate: returns animals from earliest to latest")
    void shouldSortByIntakeDateAscending() {
        assertThat(sorter.sortByIntakeDate(List.of(buddy,luna,max,bella)))
                .extracting(Animal::getIntakeDate)
                .containsExactly(
                        LocalDate.of(2026, 1, 5),
                        LocalDate.of(2026, 1, 10),
                        LocalDate.of(2026, 1, 15),
                        LocalDate.of(2026, 1, 20));
    }

    // --- sortBySpeciesThenAgeDescending ---

    @Test
    @DisplayName("sortBySpeciesThenAgeDescending: groups by species then orders by age desc")
    void shouldSortBySpeciesThenAgeDesc() {
        assertThat(sorter.sortBySpeciesThenAgeDescending(List.of(buddy,luna,max,bella)))
                .extracting(Animal::getName)
                .containsExactly("Luna", "Bella", "Max", "Buddy");
    }
}
