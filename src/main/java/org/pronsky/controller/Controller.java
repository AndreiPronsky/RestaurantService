package org.pronsky.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Controller {
    void doGet(HttpServletRequest req, HttpServletResponse resp);

    void doPost(HttpServletRequest req, HttpServletResponse resp);

    void doPut(HttpServletRequest req, HttpServletResponse resp);

    void doDelete(HttpServletRequest req, HttpServletResponse resp);
}
