-- Flyway migration for test_case and test_result tables
CREATE TABLE IF NOT EXISTS test_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS test_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_case_id BIGINT,
    status VARCHAR(50),
    executed_at DATETIME,
    CONSTRAINT fk_test_case FOREIGN KEY (test_case_id) REFERENCES test_case(id)
);
