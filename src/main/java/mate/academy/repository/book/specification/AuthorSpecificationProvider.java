package mate.academy.repository.book.specification;

import java.util.Arrays;
import mate.academy.model.book.Book;
import mate.academy.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "author";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder)
                -> root.get("author").in(Arrays.stream(params).toArray()));
    }
}
