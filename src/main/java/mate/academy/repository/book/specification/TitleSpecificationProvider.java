package mate.academy.repository.book.specification;

import java.util.Arrays;
import mate.academy.model.book.Book;
import mate.academy.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder)
                -> root.get("title").in(Arrays.stream(params).toArray()));
    }
}
