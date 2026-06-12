-- =============================================
-- V20: Add FK constraints for products.category and products.unit
-- =============================================
-- products.category references categories(id)
-- products.unit references units(id)
-- WARNING: If existing data has category/unit values not in the
-- reference tables, the ALTER will fail. Review data first.

ALTER TABLE products
    ADD CONSTRAINT fk_products_category
    FOREIGN KEY (category) REFERENCES categories (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_unit
    FOREIGN KEY (unit) REFERENCES units (id);
