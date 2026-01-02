package mate.academy.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.model.Book;
import mate.academy.repository.SpecificationBuilder;
import mate.academy.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> buildSpecification(BookSearchParameters searchParameters) {
        Specification<Book> specification = Specification.where(
                (root, query, cb) -> null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification.and(
                    bookSpecificationProviderManager
                            .getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification.and(
                    bookSpecificationProviderManager
                            .getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }
        return specification;
    }
}
