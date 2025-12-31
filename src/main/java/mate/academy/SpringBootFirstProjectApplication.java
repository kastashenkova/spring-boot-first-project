package mate.academy;

import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class SpringBootFirstProjectApplication {

    @Autowired
    private BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFirstProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            Book book1 = new Book();
            book1.setTitle("I See You're Interested in Darkness");
            book1.setAuthor("Illarion Pavliuk");
            book1.setIsbn("978-617-679-832-3");
            book1.setPrice(BigDecimal.valueOf(600));
            book1.setDescription("About impenetrable"
                    + "human indifference and the darkness within us");
            book1.setCoverImage("cover1.jpg");
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setTitle("Tango of Death");
            book2.setAuthor("Yury Vynnychuk");
            book2.setIsbn("978-617-585-236-1");
            book2.setPrice(BigDecimal.valueOf(440));
            book2.setDescription("Masterpiece about the magic of pre-war Lviv");
            book2.setCoverImage("cover2.jpg");
            bookRepository.save(book2);

            bookRepository.findAll().forEach(
                    b -> System.out.println(b.getTitle()
                            + ", " + b.getAuthor() + ", " + b.getDescription()
                            + ", " + b.getIsbn() + ", " + b.getPrice()
                            + "₴, " + b.getCoverImage()));
        };
    }
}
