package mate.academy;

import mate.academy.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
            Book darknessBook = new Book();
            darknessBook.setTitle("I See You're Interested in Darkness");
            darknessBook.setAuthor("Illarion Pavliuk");
            darknessBook.setIsbn("978-617-679-832-3");
            darknessBook.setPrice(BigDecimal.valueOf(600));
            darknessBook.setDescription("About impenetrable"
                    + "human indifference and the darkness within us");
            darknessBook.setCoverImage("cover1.jpg");
            bookRepository.save(darknessBook);

            Book tangoBook = new Book();
            tangoBook.setTitle("Tango of Death");
            tangoBook.setAuthor("Yury Vynnychuk");
            tangoBook.setIsbn("978-617-585-236-1");
            tangoBook.setPrice(BigDecimal.valueOf(440));
            tangoBook.setDescription("Masterpiece about the magic of pre-war Lviv");
            tangoBook.setCoverImage("cover2.jpg");
            bookRepository.save(tangoBook);

            bookRepository.findAll().forEach(
                    b -> System.out.println(b.getTitle()
                            + ", " + b.getAuthor() + ", " + b.getDescription()
                            + ", " + b.getIsbn() + ", " + b.getPrice()
                            + "₴, " + b.getCoverImage()));
        };
    }
}
