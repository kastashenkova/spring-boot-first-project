package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.exception.DataProcessingException;
import mate.academy.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public BookRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.persist(book);
            tx.commit();
            return book;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DataProcessingException("Could not save book: " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book b", Book.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Could not find all books", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Book b WHERE b.id = :id", Book.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Could not find book by id: " + id, e);
        }
    }
}
