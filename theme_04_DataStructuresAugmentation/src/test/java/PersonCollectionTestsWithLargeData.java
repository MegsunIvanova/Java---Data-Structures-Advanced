import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonCollectionTestsWithLargeData {

    private PersonCollection people;

    @Before
    public void setUp() {
        this.people = new PersonCollectionImpl();
    }

    private void addPeople(int count) {
        for (int i = 0; i < count; i++) {
            Person person = new Person();
            person.setEmail("pesho" + i + "@gmail" + (i % 100) + ".com");
            person.setName("Pesho" + (i % 100));
            person.setAge(i % 100);
            person.setTown("Sofia" + (i % 100));

            this.people.add(person.getEmail(), person.getName(), person.getAge(), person.getTown());
        }
    }

    @Test
    public void testAddPerson() {
        // Act
        addPeople(5000);
        Assert.assertEquals(5000, this.people.getCount());
    }

    @Test
    public void testFindPerson() {
        // Arrange
        addPeople(5000);

        // Act
        for (int i = 0; i < 100000; i++) {
            Person existingPerson = this.people.find("pesho1@gmail1.com");
            Assert.assertNotNull(existingPerson);
            Person nonExistingPerson = this.people.find("non-existing email");
            Assert.assertNull(nonExistingPerson);
        }
    }

    @Test
    public void testFindByEmailDomain() {
        // Arrange
        addPeople(5000);

        // Act
        for (int i = 0; i < 10000; i++) {
            Iterable<Person> allPeople =
                    this.people.findAll("gmail1.com");
            int count = 0;
            for (Person existingPerson : allPeople) {
                count++;
            }

            Assert.assertEquals(50, count);

            Iterable<Person> notExistingPeople =
                    this.people.findAll("non-existing email");
            count = 0;
            for (Person notExistingPerson : notExistingPeople) {
                count++;
            }
            Assert.assertEquals(0, count);
        }
    }

    @Test
    public void testFindByAgeRange() {
        // Arrange
        addPeople(5000);

        // Act
        for (int i = 0; i < 2000; i++) {
            Iterable<Person> existingpeople =
                    this.people.findAll(20, 21);
            int count = 0;
            for (Person existingPerson : existingpeople) {
                count++;
            }

            Assert.assertEquals(100, count);

            Iterable<Person> notExistingPeople =
                    this.people.findAll(500, 600);

            count = 0;
            for (Person notExistingPerson : notExistingPeople) {
                count++;
            }

            Assert.assertEquals(0, count);
        }
    }

    @Test
    public void testFindByTownAndAgeRange() {
        // Arrange
        addPeople(5000);

        // Act
        for (int i = 0; i < 5000; i++) {
            Iterable<Person> existingPeople =
                    this.people.findAll(18, 22, "Sofia20");
            int count = 0;
            for (Person existingPerson : existingPeople) {
                count++;
            }

            Assert.assertEquals(50, count);

            Iterable<Person> notExistingTownPeople =
                    this.people.findAll(20, 30, "Missing town");
            count = 0;
            for (Person notExistingTownPerson : notExistingTownPeople) {
                count++;
            }

            Assert.assertEquals(0, count);

            Iterable<Person> notExistingAgePeople =
                    this.people.findAll(200, 300, "Sofia1");

            count = 0;
            for (Person notExistingAgePerson : notExistingAgePeople) {
                count++;
            }

            Assert.assertEquals(0, count);
        }
    }
}
