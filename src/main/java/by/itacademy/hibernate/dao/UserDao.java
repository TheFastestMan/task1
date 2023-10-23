package by.itacademy.hibernate.dao;

import by.itacademy.hibernate.entity.Payment;
import by.itacademy.hibernate.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */

//    public List<User> findAll(Session session) {
//        return Collections.emptyList();
//    }

    public List<User> findAll(Session session) {
        String hql = "FROM User";
        Query<User> query = session.createQuery(hql, User.class);

        return query.getResultList();
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


    public static UserDao getInstance() {
        return INSTANCE;
    }
}