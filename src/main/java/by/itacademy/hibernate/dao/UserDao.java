package by.itacademy.hibernate.dao;

import by.itacademy.hibernate.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.cfg.JPAIndexHolder;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */

//    public List<User> findAll(Session session) {
//        return Collections.emptyList();
//    }

//    public List<User> findAll(Session session) {
//        String hql = "FROM User";
//        Query<User> query = session.createQuery(hql, User.class);
//
//        return query.getResultList();
//    }
    public List<User> findAll(Session session) {
        return new JPAQuery<User>(session).select(QUser.user).from(QUser.user).fetch();
    }


    /**
     * Возвращает всех сотрудников с указанным именем
     */
//    public List<User> findAllByFirstName(Session session, String firstName) {
//        return Collections.emptyList();
//    }
    public List<User> findAllByFirstName(Session session, String firstName) {
        String hql = "FROM User u WHERE u.personalInfo.firstname = :firstName";
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("firstName", firstName);

        return query.getResultList();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
//    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        return Collections.emptyList();
//    }
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        String hql = "FROM User u ORDER BY u.personalInfo.birthDate ASC";
        Query<User> query = session.createQuery(hql, User.class);
        query.setMaxResults(limit);

        return query.getResultList();
    }


    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
//    public List<User> findAllByCompanyName(Session session, String companyName) {
//        return Collections.emptyList();
//    }
    public List<User> findAllByCompanyName(Session session, String companyName) {
        String hql = "FROM User u WHERE u.company.name = :companyName";
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("companyName", companyName);

        return query.getResultList();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
//    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
//        return Collections.emptyList();
//    }
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        String hql = "SELECT p " +
                "FROM Payment p " +
                "JOIN FETCH p.receiver u " +
                "JOIN u.company c " +
                "WHERE c.name = :companyName " +
                "ORDER BY u.username ASC, p.amount ASC";
        Query<Payment> query = session.createQuery(hql, Payment.class);
        query.setParameter("companyName", companyName);

        return query.getResultList();
    }


    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
//    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
//        return Double.NaN;
//    }
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        String hql = "SELECT AVG(p.amount) " +
                "FROM Payment p " +
                "JOIN p.receiver u " +
                "WHERE u.personalInfo.firstname = :firstName " +
                "AND u.personalInfo.lastname = :lastName";

        Query<Double> query = session.createQuery(hql, Double.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);

        return query.getSingleResult();
    }


    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
//    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
//        return Collections.emptyList();
//    }
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        String hql = "SELECT u.company.name, AVG(p.amount) " +
                "FROM User u " +
                "LEFT JOIN u.payments p " +
                "GROUP BY u.company.name " +
                "ORDER BY u.company.name ASC";

        Query<Object[]> query = session.createQuery(hql);
        return query.getResultList();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
//    public List<Object[]> isItPossible(Session session) {
//        return Collections.emptyList();
//    }
    public List<Object[]> isItPossible(Session session) {
        String hql = "SELECT u, COALESCE(AVG(p.amount), 0.0) " +
                "FROM User u " +
                "LEFT JOIN u.payments p " +
                "GROUP BY u " +
                "HAVING COALESCE(AVG(p.amount), 0.0) > 0.0 " +
                "ORDER BY u.personalInfo.firstname ASC";

        Query<Object[]> query = session.createQuery(hql);
        return query.getResultList();
    }
//////////

    /**
     * Возвращает список: размер выплат для всех людей из всех компаний
     */
    public List<Integer> findUserSalaries(Session session) {
        QPayment payment = QPayment.payment;
        JPAQuery<Integer> query = new JPAQueryFactory(session)
                .select(payment.amount)
                .from(payment);

        return query.fetch();
    }

    /**
     * Возвращает список: самый высокий размер выплаты среди всех сотрудников
     */
    public Integer findHighestPaymentAmongAllUsers(Session session) {
        QPayment payment = QPayment.payment;
        NumberExpression<Integer> maxAmount = payment.amount.max();

        Integer highestPayment = new JPAQueryFactory(session)
                .select(maxAmount)
                .from(payment)
                .fetchOne();

        return highestPayment;
    }

    /**
     * Возвращает список: самый высокий размер выплаты среди всех сотрудников
     */
    public List<Tuple> findRolesOfAllUsers(Session session) {
        QUser user = QUser.user;

        JPAQuery<Tuple> query = new JPAQuery<>(session);
        return query
                .from(user)
                .groupBy(user.role)
                .select(user.role, user.count())
                .fetch();
    }

    /**
     * Возвращает список: ЯП сотрудника по имени и фамилии
     */
    public List<String> findLanguageByFirstAndLastName(Session session, String firstName, String lastName) {
        QUser user = QUser.user;

        JPAQuery<String> query = new JPAQuery<>(session);
        return query
                .from(user)
                .where(user.personalInfo.firstname.contains(firstName),
                        user.personalInfo.lastname.contains(lastName))
                .groupBy(user.profile.language)
                .select(user.profile.language)
                .fetch();
    }

    /**
     * Возвращает: возраст рождения сотрудника
     */
    public long findAgeByLastName(Session session, String lastName) {
        QUser user = QUser.user;

        JPAQuery<Birthday> query = new JPAQuery<>(session);
        List<Birthday> birthdays = query
                .from(user)
                .where(user.personalInfo.lastname.contains(lastName))
                .select(user.personalInfo.birthDate)
                .fetch();

        if (birthdays.isEmpty()) {
            return -1;
        }

        Birthday firstBirthday = birthdays.get(0);
        return firstBirthday.getAge();
    }


        public static UserDao getInstance() {
        return INSTANCE;
    }
}