package mate.academy.repository;

import mate.academy.repository.book.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> buildSpecification(BookSearchParameters searchParameters);
}
