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
VALUES ('Testing'),
       ('BackEnd'),
       ('Frontend'),
       ('Security');
--
INSERT INTO employees (employee_firstname, employee_lastname, department_id)
VALUES ('Alex', 'Johnson', 1),
       ('Max', 'Smith', 2),
       ('Andrew', 'Jones', 3),
       ('John', 'Williams', 3),
       ('Kate', 'Davis', 3),
       ('Anna', 'Black', 4),
       ('Mark', 'Miller', 4);
--
INSERT INTO projects (project_name)
VALUES ('Chatting App'),
       ('Rest Service'),
       ('Pay Service'),
       ('Log Service');

INSERT INTO employees_projects (employee_id, project_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 2),
       (6, 1),
       (6, 3),
       (7, 4);
--


