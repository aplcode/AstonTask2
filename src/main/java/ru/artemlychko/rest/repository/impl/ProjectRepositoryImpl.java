package ru.artemlychko.rest.repository.impl;

import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.db.ConnectionManagerImpl;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.exception.RepositoryException;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.ProjectRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepositoryImpl implements ProjectRepository {
    private static final String SAVE_SQL = """
            INSERT INTO projects (project_name)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE projects
            SET project_name = ?
            WHERE project_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM projects
            WHERE project_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT project_id, project_name FROM projects
            WHERE project_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT project_id, project_name FROM projects;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM projects
                        WHERE project_id = ?
                        LIMIT 1);
            """;
    private static ProjectRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final EmployeeToProjectRepository employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();

    private ProjectRepositoryImpl() {
    }

    public static synchronized ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepositoryImpl();
        }
        return instance;
    }

    private static Project createProject(ResultSet resultSet) throws SQLException {
        Project project;
        project = new Project(
                resultSet.getLong("project_id"),
                resultSet.getString("project_name"),
                null);
        return project;
    }

    @Override
    public Project save(Project project) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, project.getName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                project = new Project(
                        resultSet.getLong("project_id"),
                        project.getName(),
                        employeeToProjectRepository.findEmployeesByProjectId(resultSet.getLong("project_id"))
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return project;
    }

    @Override
    public void update(Project project) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, project.getName());
            preparedStatement.setLong(2, project.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            employeeToProjectRepository.deleteByProjectId(id);
            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public Optional<Project> findById(Long id) {
        Project project = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                project = createProject(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(project);
    }

    @Override
    public List<Project> findAll() {
        List<Project> projectList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projectList.add(createProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return projectList;
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
}
