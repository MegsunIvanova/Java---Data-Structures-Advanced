import java.util.*;
import java.util.stream.Collectors;

public class PersonCollectionImpl implements PersonCollection {

    Map<String, Person> peopleByEmail;
    //Indices
    Map<String, TreeMap<String, Person>> peopleByDomainThanByEmail;
    Map<NameTownKey, TreeMap<String, Person>> peopleByNameTownKeyThanByEmail;
    Map<String, TreeMap<Integer, TreeMap<String, Person>>> peopleByTownThanByAgeThanByEmail;

    public PersonCollectionImpl() {
        this.peopleByEmail = new HashMap<>();
        this.peopleByDomainThanByEmail = new HashMap<>();
        this.peopleByNameTownKeyThanByEmail = new HashMap<>();
        this.peopleByTownThanByAgeThanByEmail = new HashMap<>();
    }

    private static class NameTownKey {
        private final String name;
        private final String town;

        public NameTownKey(String name, String town) {
            this.name = name;
            this.town = town;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof NameTownKey)) return false;
            NameTownKey other = (NameTownKey) obj;
            return name.equals(other.name) && town.equals(other.town);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name) + 73 * Objects.hashCode(town);
        }
    }

    @Override
    public boolean add(String email, String name, int age, String town) {
        if (peopleByEmail.containsKey(email)) {
            return false;
        }

        Person person = new Person(email, name, age, town);
        peopleByEmail.put(email, person);

        addToIndices(person);

        return true;
    }

    @Override
    public int getCount() {
        return peopleByEmail.size();
    }

    @Override
    public boolean delete(String email) {
        Person deletedValue = peopleByEmail.remove(email);
        if (deletedValue == null) {
            return false;
        }

        removeFromIndices(deletedValue);
        return true;
    }

    @Override
    public Person find(String email) {
        return this.peopleByEmail.get(email);
    }

    @Override
    public Iterable<Person> findAll(String emailDomain) {
        TreeMap<String, Person> result = peopleByDomainThanByEmail.get(emailDomain);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.values();
    }

    @Override
    public Iterable<Person> findAll(String name, String town) {
        NameTownKey nameTownKey = new NameTownKey(name, town);
        TreeMap<String, Person> result = peopleByNameTownKeyThanByEmail.get(nameTownKey);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.values();
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge) {
        TreeSet<Person> result = new TreeSet<>();
        for (String town : peopleByTownThanByAgeThanByEmail.keySet()) {
            result.addAll(getListOfPeopleFilteredByAgeAndTown(startAge, endAge, town));
        }
        return result;
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge, String town) {
        return getListOfPeopleFilteredByAgeAndTown(startAge, endAge, town);
    }

    private List<Person> getListOfPeopleFilteredByAgeAndTown(int startAge, int endAge, String town) {
        TreeMap<Integer, TreeMap<String, Person>> peopleFromCurrTown = peopleByTownThanByAgeThanByEmail.get(town);
        if (peopleFromCurrTown == null) {
            return new ArrayList<>();
        }

        Map<Integer, TreeMap<String, Person>> peopleByCurrAge = peopleFromCurrTown.subMap(startAge, endAge + 1);

        return peopleByCurrAge.values().stream()
                .map(TreeMap::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    private void addToIndices(Person p) {
        String domain = getDomainByEmail(p.getEmail());
        addToIndices(domain, peopleByDomainThanByEmail, p);

        NameTownKey nameTownKey = getNameTownKey(p);
        addToIndices(nameTownKey, peopleByNameTownKeyThanByEmail, p);


        String town = p.getTown();
        int age = p.getAge();
        peopleByTownThanByAgeThanByEmail.putIfAbsent(town, new TreeMap<>());
        TreeMap<Integer, TreeMap<String, Person>> currTownPeopleByByAgeThanByEmail =
                peopleByTownThanByAgeThanByEmail.get(town);
        addToIndices(age, currTownPeopleByByAgeThanByEmail, p);
    }

    private <K> void addToIndices(K key, Map<K, TreeMap<String, Person>> peopleMap, Person p) {
        peopleMap.putIfAbsent(key, new TreeMap<>());
        TreeMap<String, Person> peopleInCurrentKey = peopleMap.get(key);
        peopleInCurrentKey.put(p.getEmail(), p);
    }

    private void removeFromIndices(Person p) {
        String domain = getDomainByEmail(p.getEmail());
        removeFromIndices(domain, peopleByDomainThanByEmail, p);

        NameTownKey nameTownKey = getNameTownKey(p);
        removeFromIndices(nameTownKey, peopleByNameTownKeyThanByEmail, p);

        String town = p.getTown();
        int age = p.getAge();
        TreeMap<Integer, TreeMap<String, Person>> currTownPeopleByByAgeThanByEmail =
                peopleByTownThanByAgeThanByEmail.get(town);
        removeFromIndices(age, currTownPeopleByByAgeThanByEmail, p);
       /* if (currentTownPeopleByByAgeThanByEmail.isEmpty()) {
            peopleByTownThanByAgeThanByEmail.remove(town);
        }*/
    }

    private <K> void removeFromIndices(K key, Map<K, TreeMap<String, Person>> peopleMap, Person p) {
        TreeMap<String, Person> peopleInCurrentKey = peopleMap.get(key);
        if (peopleInCurrentKey != null) {
            peopleInCurrentKey.remove(p.getEmail());
            /* if (peopleInCurrentKey.isEmpty()) {
                 peopleMap.remove(domain);
                }*/
        }
    }

    private String getDomainByEmail(String email) {
        return email.substring(email.lastIndexOf('@') + 1);
    }

    private NameTownKey getNameTownKey(Person p) {
        return new NameTownKey(p.getName(), p.getTown());
    }
}
