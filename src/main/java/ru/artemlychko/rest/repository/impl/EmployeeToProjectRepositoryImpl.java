package ru.artemlychko.rest.repository.impl;

import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.db.ConnectionManagerImpl;
import ru.artemlychko.rest.exception.RepositoryException;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.ProjectRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeToProjectRepositoryImpl implements EmployeeToProjectRepository{
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final ProjectRepository projectRepository = ProjectRepositoryImpl.getInstance();
    private static final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();

    private static final String SAVE_SQL = """
            INSERT INTO employees_projects (employee_id, project_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE employees_projects
            SET employee_id = ?,
                project_id = ?
            WHERE employees_projects_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employees_projects
            WHERE employees_projects_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT employees_projects_id, employee_id, project_id FROM employees_projects
            WHERE employees_projects_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT employees_projects_id, employee_id, project_id FROM employees_projects;
            """;
    private static final String FIND_ALL_BY_EMPLOYEE_ID_SQL = """
            SELECT employees_projects_id, employee_id, project_id FROM employees_projects
            WHERE employee_id = ?;
            """;
    private static final String FIND_ALL_BY_PROJECT_ID_SQL = """
            SELECT employees_projects_id, employee_id, project_id FROM employees_projects
            WHERE project_id = ?;
            """;
    private static final String FIND_BY_EMPLOYEE_ID_AND_PROJECT_ID_SQL = """
            SELECT employees_projects_id, employee_id, project_id FROM employees_projects
            WHERE employee_id = ? AND project_id = ?
            LIMIT 1;
            """;
    private static final String DELETE_BY_EMPLOYEE_ID_SQL = """
            DELETE FROM employees_projects
            WHERE employee_id = ?;
            """;
    private static final String DELETE_BY_PROJECT_ID_SQL = """
            DELETE FROM employees_projects
            WHERE project_id = ?;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employees_projects
                        WHERE employees_projects_id = ?
                        LIMIT 1);
            """;
    private static EmployeeToProjectRepository instance;

    private EmployeeToProjectRepositoryImpl() {
    }

    public static synchronized EmployeeToProjectRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeToProjectRepositoryImpl();
        }
        return instance;
    }

    private static EmployeeToProject createEmployeeToProject(ResultSet resultSet) throws SQLException {
        EmployeeToProject employeeToProject;
        employeeToProject = new EmployeeToProject(
                resultSet.getLong("employees_projects_id"),
                resultSet.getLong("employee_id"),
                resultSet.getLong("project_id")
        );
        return employeeToProject;
    }

    @Override
    public EmployeeToProject save(EmployeeToProject employeeToProject) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, employeeToProject.getEmployeeId());
            preparedStatement.setLong(2, employeeToProject.getProjectId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employeeToProject = new EmployeeToProject(
                        resultSet.getLong("employees_projects_id"),
                        employeeToProject.getEmployeeId(),
                        employeeToProject.getProjectId()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return employeeToProject;
    }

    @Override
    public void update(EmployeeToProject employeeToProject) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setLong(1, employeeToProject.getEmployeeId());
            preparedStatement.setLong(2, employeeToProject.getProjectId());
            preparedStatement.setLong(3, employeeToProject.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByEmployeeId(Long employeeId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_EMPLOYEE_ID_SQL);) {

            preparedStatement.setLong(1, employeeId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByProjectId(Long projectId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_PROJECT_ID_SQL);) {

            preparedStatement.setLong(1, projectId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public Optional<EmployeeToProject> findById(Long id) {
        EmployeeToProject employeeToProject = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeToProject = createEmployeeToProject(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(employeeToProject);
    }

    @Override
    public List<EmployeeToProject> findAll() {
        List<EmployeeToProject> userToDepartmentList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userToDepartmentList.add(createEmployeeToProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userToDepartmentList;
    }

    @Override
    public boolean existsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    public List<EmployeeToProject> findAllByEmployeeId(Long employeeId) {
        List<EmployeeToProject> userToDepartmentList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userToDepartmentList.add(createEmployeeToProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userToDepartmentList;
    }

    @Override
    public List<Project> findProjectsByEmployeeId(Long employeeId) {
        List<Project> projectList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long projectId = resultSet.getLong("project_id");
                Optional<Project> optionalProject = projectRepository.findById(projectId);
                optionalProject.ifPresent(projectList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return projectList;
    }

    public List<EmployeeToProject> findAllByProjectId(Long projectId) {
        List<EmployeeToProject> userToDepartmentList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PROJECT_ID_SQL)) {

            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userToDepartmentList.add(createEmployeeToProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userToDepartmentList;
    }

    public List<Employee> findEmployeesByProjectId(Long projectId) {
        List<Employee> userList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PROJECT_ID_SQL)) {

            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long employeeId = resultSet.getLong("employee_id");
                Optional<Employee> optionalUser = employeeRepository.findById(employeeId);
                optionalUser.ifPresent(userList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userList;
    }

    @Override
    public Optional<EmployeeToProject> findByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        Optional<EmployeeToProject> userToDepartment = Optional.empty();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMPLOYEE_ID_AND_PROJECT_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            preparedStatement.setLong(2, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userToDepartment = Optional.of(createEmployeeToProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return userToDepartment;
    }


}
