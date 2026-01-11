-- Припустимо, що після вставки categories та books, їх id будуть 1,2,3...
INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b, categories c
WHERE b.isbn = '978-617-679-847-6' AND c.name = 'Fantasy';

INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b, categories c
WHERE b.isbn = '978-617-679-921-3' AND c.name = 'Fantasy';

INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b, categories c
WHERE b.isbn = '978-966-03-9215-7' AND c.name = 'Historical';

INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b, categories c
WHERE b.isbn = '978-0000000001' AND c.name = 'Poetry';

INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b, categories c
WHERE b.isbn = '978-617-679-840-6' AND c.name = 'Poetry';