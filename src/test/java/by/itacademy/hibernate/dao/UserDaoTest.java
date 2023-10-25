package by.itacademy.hibernate.dao;


import by.itacademy.hibernate.entity.*;
import by.itacademy.hibernate.utils.TestDataImporter;
import by.itacademy.hibernate.util.HibernateUtil;
import com.querydsl.core.Tuple;
import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static by.itacademy.hibernate.entity.QUser.user;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class UserDaoTest {

    private final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    private final UserDao userDao = UserDao.getInstance();

    @BeforeAll
    public void initDb() {
        TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public void finish() {
        sessionFactory.close();
    }

    @Test
    void findAll() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAll(session);
        assertThat(results).hasSize(5);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Tim Cook", "Diane Greene");

        session.getTransaction().commit();
    }

    @Test
    void findAllByFirstName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAllByFirstName(session, "Bill");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).fullName()).isEqualTo("Bill Gates");

        session.getTransaction().commit();
    }

    @Test
    void findLimitedUsersOrderedByBirthday() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        int limit = 3;
        List<User> results = userDao.findLimitedUsersOrderedByBirthday(session, limit);
        assertThat(results).hasSize(limit);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).contains("Diane Greene", "Steve Jobs", "Bill Gates");

        session.getTransaction().commit();
    }

    @Test
    void findAllByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAllByCompanyName(session, "Google");
        assertThat(results).hasSize(2);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Sergey Brin", "Diane Greene");

        session.getTransaction().commit();
    }

    @Test
    void findAllPaymentsByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Payment> applePayments = userDao.findAllPaymentsByCompanyName(session, "Apple");
        assertThat(applePayments).hasSize(5);

        List<Integer> amounts = applePayments.stream().map(Payment::getAmount).collect(toList());
        assertThat(amounts).contains(250, 500, 600, 300, 400);

        session.getTransaction().commit();
    }

    @Test
    void findAveragePaymentAmountByFirstAndLastNames() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Double averagePaymentAmount = userDao.findAveragePaymentAmountByFirstAndLastNames(session, "Bill", "Gates");
        assertThat(averagePaymentAmount).isEqualTo(300.0);

        session.getTransaction().commit();
    }

    @Test
    void findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> results = userDao.findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(session);
        assertThat(results).hasSize(3);

        List<String> orgNames = results.stream().map(a -> (String) a[0]).collect(toList());
        assertThat(orgNames).contains("Apple", "Google", "Microsoft");

        List<Double> orgAvgPayments = results.stream().map(a -> (Double) a[1]).collect(toList());
        assertThat(orgAvgPayments).contains(410.0, 400.0, 300.0);

        session.getTransaction().commit();
    }

    @Test
    void isItPossible() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> results = userDao.isItPossible(session);
        assertThat(results).hasSize(5);

        List<String> names = results.stream().map(r -> ((User) r[0]).fullName()).collect(toList());
        assertThat(names).contains("Sergey Brin", "Steve Jobs");

        List<Double> averagePayments = results.stream().map(r -> (Double) r[1]).collect(toList());
        assertThat(averagePayments).contains(500.0, 450.0);

        session.getTransaction().commit();
    }

    ////

    @Test
    void findUserSalaries() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Integer> salaries = userDao.findUserSalaries(session);

        assertThat(salaries).hasSize(14);
        assertThat(salaries).contains(100, 300, 500, 250,
                600, 500, 400, 300,
                500, 500, 500, 300,
                300, 300
        );

        session.getTransaction().commit();
    }

    @Test
    void findHighestPaymentAmongAllUsers() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Integer highestPayment = userDao.findHighestPaymentAmongAllUsers(session);
        assertThat(highestPayment).isEqualTo(600);

        session.getTransaction().commit();
    }

    @Test
    void findRolesOfAllUsers() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Tuple> roles = userDao.findRolesOfAllUsers(session);

        assertThat(roles).isNotNull();
        assertThat(roles).isNotEmpty();

        assertThat(roles).allSatisfy(tuple -> {
            Role role = tuple.get(user.role);
            assertThat(role).isNotNull();
            assertThat(role).isIn(Role.values());
        });

        session.getTransaction().commit();
    }

    @Test
    public void testFindLanguagesByFirstAndLastName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        String firstName = "Sergey";
        String lastName = "Brin";
        List<String> languages = userDao.findLanguageByFirstAndLastName(session, firstName, lastName);

        assertEquals(1, languages.size());

        session.getTransaction().commit();
    }


    @Test
    public void testFindAgeByLastName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        String lastName = "Brin";
        long age = userDao.findAgeByLastName(session, lastName);

        assertEquals(50, age);

        session.getTransaction().commit();
    }
}