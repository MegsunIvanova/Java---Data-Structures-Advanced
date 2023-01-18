public interface PersonCollection {

    boolean add(String email, String name, int age, String town);

    int getCount();

    boolean delete(String email);

    Person find(String email);

    Iterable<Person> findAll(String emailDomain);

    Iterable<Person> findAll(String name, String town);

    Iterable<Person> findAll(int startAge, int endAge);

    Iterable<Person> findAll(int startAge, int endAge, String town);
}
