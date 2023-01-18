import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonCollectionUnitTests {

    private PersonCollection people;

    @Before
    public void setUp() {
        this.people = new PersonCollectionImpl();
    }

    @Test
    public void AddPerson_ShouldWorkCorrectly() {
        // Arrange

        // Act
        boolean isAdded =
                this.people.add("pesho@gmail.com", "Peter", 18, "Sofia");

        // Assert
        Assert.assertTrue(isAdded);
        Assert.assertEquals(1, this.people.getCount());
    }

    @Test
    public void AddPerson_DuplicatedEmail_ShouldWorkCorrectly() {
        // Arrange

        // Act
        boolean isAddedFirst =
                this.people.add("pesho@gmail.com", "Peter", 18, "Sofia");
        boolean isAddedSecond =
                this.people.add("pesho@gmail.com", "Maria", 24, "Plovdiv");

        // Assert
        Assert.assertTrue(isAddedFirst);
        Assert.assertFalse(isAddedSecond);
        Assert.assertEquals(1, this.people.getCount());
    }

    @Test
    public void FindPerson_ExistingPerson_ShouldReturnPerson() {
        // Arrange
        this.people.add("pesho@gmail.com", "Peter", 28, "Plovdiv");

        // Act
        Person person = this.people.find("pesho@gmail.com");

        // Assert
        Assert.assertNotNull(person);
    }

    @Test
    public void FindPerson_NonExistingPerson_ShouldReturnNothing() {
        // Arrange

        // Act
        Person person = this.people.find("pesho@gmail.com");

        // Assert
        Assert.assertNull(person);
    }

    @Test
    public void DeletePerson_ShouldWorkCorrectly() {
        // Arrange
        this.people.add("pesho@gmail.com", "Peter", 28, "Plovdiv");

        // Act
        boolean isDeletedExisting =
                this.people.delete("pesho@gmail.com");
        boolean isDeletedNonExisting =
                this.people.delete("pesho@gmail.com");

        // Assert
        Assert.assertTrue(isDeletedExisting);
        Assert.assertFalse(isDeletedNonExisting);
        Assert.assertEquals(0, people.getCount());
    }

    @Test
    public void FindByEmailDomain_ShouldReturnMatching() {
        // Arrange
        this.people.add("pesho@gmail.com", "Pesho", 28, "Plovdiv");
        this.people.add("kiro@yahoo.co.uk", "Kiril", 22, "Sofia");
        this.people.add("mary@gmail.com", "Maria", 21, "Plovdiv");
        this.people.add("ani@gmail.com", "Anna", 19, "Bourgas");

        // Act
        Iterable<Person> byGmail = this.people.findAll("gmail.com");
        Iterable<Person> byYahoo = this.people.findAll("yahoo.co.uk");
        Iterable<Person> byHoo = this.people.findAll("hoo.co.uk");

        List<String> personGmailResult = new ArrayList<>();
        byGmail.forEach(e -> personGmailResult.add(e.getEmail()));

        List<String> personYahooResult = new ArrayList<>();
        byYahoo.forEach(e -> personYahooResult.add(e.getEmail()));

        List<String> personHooResult = new ArrayList<>();
        byHoo.forEach(e -> personHooResult.add(e.getEmail()));

        String[] gmailResult = new String[personGmailResult.size()];
        for (int i = 0; i < gmailResult.length; i++) {
            gmailResult[i] = personGmailResult.get(i);
        }

        String[] yahooResult = new String[personYahooResult.size()];
        for (int i = 0; i < yahooResult.length; i++) {
            yahooResult[i] = personYahooResult.get(i);
        }

        String[] hooResult = new String[personHooResult.size()];
        for (int i = 0; i < hooResult.length; i++) {
            hooResult[i] = personHooResult.get(i);
        }

        // Assert
        Assert.assertArrayEquals(
                new String[]{"ani@gmail.com", "mary@gmail.com", "pesho@gmail.com"},
                gmailResult);

        Assert.assertArrayEquals(
                new String[]{"kiro@yahoo.co.uk"},
                yahooResult);

        Assert.assertArrayEquals(
                new String[]{},
                hooResult);
    }

    @Test
    public void FindByNameAndTown_ShouldReturnMatching() {
        // Arrange
        this.people.add("pesho@gmail.com", "Pesho", 28, "Plovdiv");
        this.people.add("kiro@yahoo.co.uk", "Kiril", 22, "Sofia");
        this.people.add("pepi@gmail.com", "Pesho", 21, "Plovdiv");
        this.people.add("ani@gmail.com", "Anna", 19, "Bourgas");
        this.people.add("pepi2@yahoo.fr", "Pesho", 21, "Plovdiv");

        // Act
        Iterable<Person> byPeshoPlovdiv = this.people.findAll("Pesho", "Plovdiv");
        Iterable<Person> byLowercase = this.people.findAll("pesho", "plovdiv");
        Iterable<Person> byPeshoNoTown = this.people.findAll("Pesho", null);
        Iterable<Person> byAnnaBourgas = this.people.findAll("Anna", "Bourgas");

        List<String> personPeshoPlovdivReslt = new ArrayList<>();
        byPeshoPlovdiv.forEach(e -> {
            personPeshoPlovdivReslt.add(e.getEmail());
        });

        List<String> peopleLowercaseResult = new ArrayList<>();
        byLowercase.forEach(e -> {
            peopleLowercaseResult.add(e.getEmail());
        });

        List<String> peoplePeshoNoTownResult = new ArrayList<>();
        byPeshoNoTown.forEach(e -> {
            peoplePeshoNoTownResult.add(e.getEmail());
        });

        List<String> peopleAnnaBourgasResult = new ArrayList<>();
        byAnnaBourgas.forEach(e -> {
            peopleAnnaBourgasResult.add(e.getEmail());
        });


        String[] peshoPlovdivReslt = new String[personPeshoPlovdivReslt.size()];
        for (int i = 0; i < peshoPlovdivReslt.length; i++) {
            peshoPlovdivReslt[i] = personPeshoPlovdivReslt.get(i);
        }

        String[] lowercaseResult = new String[peopleLowercaseResult.size()];
        for (int i = 0; i < lowercaseResult.length; i++) {
            lowercaseResult[i] = peopleLowercaseResult.get(i);
        }

        String[] peshoNoTownResult = new String[peoplePeshoNoTownResult.size()];
        for (int i = 0; i < peshoNoTownResult.length; i++) {
            peshoNoTownResult[i] = peoplePeshoNoTownResult.get(i);
        }

        String[] annaBourgasResult = new String[peopleAnnaBourgasResult.size()];
        for (int i = 0; i < annaBourgasResult.length; i++) {
            annaBourgasResult[i] = peopleAnnaBourgasResult.get(i);
        }

        Arrays.sort(peshoPlovdivReslt);
        String[] expected1 = {"pepi@gmail.com", "pepi2@yahoo.fr", "pesho@gmail.com"};
        Arrays.sort(expected1);

        // Assert
        Assert.assertArrayEquals(
                expected1,
                peshoPlovdivReslt);

        Assert.assertArrayEquals(
                new String[]{},
                lowercaseResult);

        Assert.assertArrayEquals(
                new String[]{},
                peshoNoTownResult);

        Assert.assertArrayEquals(
                new String[]{"ani@gmail.com"},
                annaBourgasResult);
    }

    @Test
    public void findByAgeRange_ShouldReturnMatching() {
        // Arrange
        this.people.add("pesho@gmail.com", "Pesho", 28, "Plovdiv");
        this.people.add("kiro@yahoo.co.uk", "Kiril", 22, "Sofia");
        this.people.add("pepi@gmail.com", "Pesho", 21, "Plovdiv");
        this.people.add("ani@gmail.com", "Anna", 19, "Bourgas");
        this.people.add("pepi2@yahoo.fr", "Pesho", 21, "Plovdiv");
        this.people.add("asen@gmail.com", "Asen", 21, "Rousse");

        // Act
        Iterable<Person> byAgedFrom21to22 = this.people.findAll(21, 22);
        Iterable<Person> byAgedFrom10to11 = this.people.findAll(10, 11);
        Iterable<Person> byAged21 = this.people.findAll(21, 21);
        Iterable<Person> byAgedFrom0to1000 = this.people.findAll(0, 1000);

        List<String> peopleAgedFrom21to22Result = new ArrayList<>();
        byAgedFrom21to22.forEach(e -> peopleAgedFrom21to22Result.add(e.getEmail()));

        List<String> peopleAgedFrom10to11Result = new ArrayList<>();
        byAgedFrom10to11.forEach(e -> peopleAgedFrom10to11Result.add(e.getEmail()));

        List<String> peopleAged21Result = new ArrayList<>();
        byAged21.forEach(e -> peopleAged21Result.add(e.getEmail()));

        List<String> peopleAgedFrom0to1000Result = new ArrayList<>();
        byAgedFrom0to1000.forEach(e -> peopleAgedFrom0to1000Result.add(e.getEmail()));

        String[] from21to22Result = new String[peopleAgedFrom21to22Result.size()];
        for (int i = 0; i < from21to22Result.length; i++) {
            from21to22Result[i] = peopleAgedFrom21to22Result.get(i);
        }

        String[] from10to11 = new String[peopleAgedFrom10to11Result.size()];
        for (int i = 0; i < from10to11.length; i++) {
            from10to11[i] = peopleAgedFrom10to11Result.get(i);
        }

        String[] aged21 = new String[peopleAged21Result.size()];
        for (int i = 0; i < aged21.length; i++) {
            aged21[i] = peopleAged21Result.get(i);
        }

        String[] agedFrom0to1000 = new String[peopleAgedFrom0to1000Result.size()];
        for (int i = 0; i < agedFrom0to1000.length; i++) {
            agedFrom0to1000[i] = peopleAgedFrom0to1000Result.get(i);
        }

        Arrays.sort(from21to22Result);
        String[] expected1 = new String[]{"asen@gmail.com", "pepi@gmail.com", "pepi2@yahoo.fr", "kiro@yahoo.co.uk"};
        Arrays.sort(expected1);

        Arrays.sort(aged21);
        String[] expected2 = new String[]{"asen@gmail.com", "pepi@gmail.com", "pepi2@yahoo.fr"};
        Arrays.sort(expected2);

        Arrays.sort(agedFrom0to1000);
        String[] expected3 = new String[]{"ani@gmail.com", "asen@gmail.com", "pepi@gmail.com", "pepi2@yahoo.fr", "kiro@yahoo.co.uk", "pesho@gmail.com"};
        Arrays.sort(expected3);

        // Assert
        Assert.assertArrayEquals(
                expected1,
                from21to22Result);


        Assert.assertArrayEquals(
                new String[]{},
                from10to11);

        Assert.assertArrayEquals(
                expected2,
                aged21);

        Assert.assertArrayEquals(
                expected3,
                agedFrom0to1000);
    }
}
