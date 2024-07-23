package org.pronsky.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.mapstruct.factory.Mappers;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.dao.impl.OrderDetailsDAOImpl;
import org.pronsky.data.dao.impl.ProductCategoryDAOImpl;
import org.pronsky.data.dao.impl.ProductDAOImpl;
import org.pronsky.data.repository.OrderDetailRepository;
import org.pronsky.data.repository.impl.OrderDetailRepositoryImpl;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.impl.OrderDetailsServiceImpl;
import org.pronsky.service.mapper.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Log4j
@NoArgsConstructor
@WebServlet("/api/order_details")
public class OrderDetailsServlet extends HttpServlet {

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARSET = "UTF-8";
    private final ConnectionUtil connectionUtil = new ConnectionUtil();
    private final ProductDAO productDAO = new ProductDAOImpl(connectionUtil);
    private final ProductCategoryDAO productCategoryDAO = new ProductCategoryDAOImpl(connectionUtil);
    private final OrderDetailsDAO orderDetailsDAO = new OrderDetailsDAOImpl(connectionUtil);
    private final OrderDetailRepository orderDetailRepository = new OrderDetailRepositoryImpl(orderDetailsDAO, productDAO, productCategoryDAO);
    private final OrderDetailsService orderDetailsService = new OrderDetailsServiceImpl(Mappers.getMapper(Mapper.class), orderDetailRepository);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Got request type GET : " + req.getRequestURI());
        Long id = processParams(req);
        if (id == null) {
            getAll(resp);
        } else {
            getOne(id, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Got request type POST : " + req.getRequestURI());
        try (BufferedReader reader = req.getReader()) {
            String payload = getPayload(reader);
            OrderDetailsDTO orderDetailsDTO = objectMapper.readValue(payload, OrderDetailsDTO.class);
            orderDetailsService.save(orderDetailsDTO);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Got request type PUT : " + req.getRequestURI());
        try (BufferedReader reader = req.getReader()) {
            String payload = getPayload(reader);
            OrderDetailsDTO orderDetailsDTO = objectMapper.readValue(payload, OrderDetailsDTO.class);
            orderDetailsService.save(orderDetailsDTO);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Got request type DELETE : " + req.getRequestURI());
        try {
            orderDetailsService.delete(Long.parseLong(req.getParameter("id")));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Long processParams(HttpServletRequest req) {
        String rawId = req.getParameter("id");
        if (rawId != null) {
            return Long.parseLong(rawId);
        } else return null;
    }

    private void getOne(Long id, HttpServletResponse resp) {
        try {
            OrderDetailsDTO orderDetailsDTO = orderDetailsService.getById(id);
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), orderDetailsDTO);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getAll(HttpServletResponse resp) {
        try {
            List<OrderDetailsDTO> orderDetails = orderDetailsService.getAll();
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), orderDetails);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String getPayload(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}
