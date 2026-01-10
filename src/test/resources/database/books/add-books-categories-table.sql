INSERT INTO books_categories (book_id, category_id) VALUES (1, (SELECT id FROM categories WHERE name = 'fantasy'));
INSERT INTO books_categories (book_id, category_id) VALUES (2, (SELECT id FROM categories WHERE name = 'fantasy'));
INSERT INTO books_categories (book_id, category_id) VALUES (3, (SELECT id FROM categories WHERE name = 'history'));
INSERT INTO books_categories (book_id, category_id) VALUES (4, (SELECT id FROM categories WHERE name = 'novel'));
INSERT INTO books_categories (book_id, category_id) VALUES (5, (SELECT id FROM categories WHERE name = 'poetry'));