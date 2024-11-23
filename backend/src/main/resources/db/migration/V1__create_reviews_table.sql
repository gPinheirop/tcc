
CREATE TABLE IF NOT EXISTS review(
    "id" BIGSERIAL PRIMARY KEY,
    "summary" TEXT,
    "text" TEXT,
    "indexed_text" TEXT,
    "time" BIGINT
);

-- Indexar coluna com dicionário 'english'
CREATE INDEX IF NOT EXISTS idx_review_indexed_text_gin ON review USING GIN (to_tsvector('english', indexed_text));
CREATE INDEX IF NOT EXISTS idx_review_summary_gin ON review USING GIN (to_tsvector('english', summary));
UPDATE review SET indexed_text = text;

-- Para calcular distancia de Levenshtein
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;

