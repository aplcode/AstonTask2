package ru.artemlychko.rest.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.db.ConnectionManagerImpl;
import ru.artemlychko.rest.util.InitSqlScheme;

import java.io.IOException;

@WebServlet(urlPatterns = {"/db"})
public class DbServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        InitSqlScheme.initSqlScheme(connectionManager);

    }
}
