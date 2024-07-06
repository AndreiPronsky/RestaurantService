package org.pronsky.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pronsky.controller.factory.ControllerFactory;

@WebServlet("/")
@Slf4j
public class FrontController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        getController(req).doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        getController(req).doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        getController(req).doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        getController(req).doDelete(req, resp);
    }

    @Override
    public void init() {
        log.info("FRONT CONTROLLER INITIALISED");
    }

    @Override
    public void destroy() {
        log.info("FRONT CONTROLLER DESTROYED");
    }

    private Controller getController(HttpServletRequest req) {
        String controllerParameter = req.getPathInfo();
        if (controllerParameter != null && controllerParameter.startsWith("/")) {
            controllerParameter = controllerParameter.substring(1);
        }
        return ControllerFactory.INSTANCE.getController(controllerParameter);
    }
}
