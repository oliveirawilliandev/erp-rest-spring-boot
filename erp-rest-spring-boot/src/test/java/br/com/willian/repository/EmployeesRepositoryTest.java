package br.com.oliveirawillian.repository;

import br.com.oliveirawillian.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.oliveirawillian.model.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace =AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository personRepository;
    private static Person person;
    @BeforeAll
    static void setUp() {
    person = new Person();
    }



    @Test
    @Order(1)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"));
        person = personRepository.FindPeopleByName("rei",pageable).getContent().get(0);
        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Perreira",person.getFirstName());
        assertEquals("Martins",person.getLastName());
        assertEquals("5481232",person.getAddress());
        assertEquals("Male",person.getGender());
        assertTrue(person.getEnabled());

    }

    @Test
    @Order(2)
    void disablePerson() {
        Long id = person.getId();
        personRepository.disablePerson(id);

        var result = personRepository.findById(id);
        person = result.get();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Perreira",person.getFirstName());
        assertEquals("Martins",person.getLastName());
        assertEquals("5481232",person.getAddress());
        assertEquals("Male",person.getGender());
        assertFalse(person.getEnabled());
    }
}