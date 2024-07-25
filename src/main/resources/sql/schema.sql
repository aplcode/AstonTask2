DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS employees_projects CASCADE;
DROP TABLE IF EXISTS projects CASCADE;

CREATE TABLE IF NOT EXISTS departments
(
    department_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS employees
(
    employee_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_firstname VARCHAR(255) NOT NULL,
    employee_lastname  VARCHAR(255) NOT NULL,
    department_id      BIGINT REFERENCES departments (department_id)
    );

CREATE TABLE IF NOT EXISTS projects
(
    project_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_name   VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS employees_projects
(
    employees_projects_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id              BIGINT REFERENCES employees (employee_id),
    project_id               BIGINT REFERENCES projects (project_id),
    CONSTRAINT unique_link UNIQUE (employee_id, project_id)
    );



INSERT INTO departments (department_name)
VALUES ('Администрация'),       -- 1
       ('BackEnd разработка'),  -- 2
       ('Frontend разработка'), -- 3
       ('HR менеджмент'); -- 4
--
INSERT INTO employees (employee_firstname, employee_lastname, department_id)
VALUES ('Иван', 'Субботин', 1),      -- 1
       ('Петр', 'Понедельников', 2), -- 2
       ('Игнат', 'Вторников', 3),    -- 3
       ('Иван', 'Середец', 3),       -- 4
       ('Максим', 'Четверкин', 3),   -- 5
       ('Вера', 'Пятницкая', 4),     -- 6
       ('Ольга', 'Воскресенская', 4); -- 7
--
INSERT INTO projects (project_name)
VALUES ('Chatting App'), -- 1
       ('Rest Service'), -- 2
       ('Pay Service'), -- 3
       ('Log Service');

INSERT INTO employees_projects (employee_id, project_id)
VALUES (1, 1), -- 1
       (2, 1), -- 2
       (3, 2), -- 3
       (4, 2), -- 4
       (5, 2), -- 5
       (6, 1), -- 6
       (6, 3), -- 6
       (7, 4); -- 7
--


