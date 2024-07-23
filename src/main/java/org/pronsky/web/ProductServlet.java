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
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.dao.impl.ProductCategoryDAOImpl;
import org.pronsky.data.dao.impl.ProductDAOImpl;
import org.pronsky.data.repository.ProductRepository;
import org.pronsky.data.repository.impl.ProductRepositoryImpl;
import org.pronsky.exceptions.UnableToFindException;
import org.pronsky.service.ProductService;
import org.pronsky.service.dto.ProductDTO;
import org.pronsky.service.impl.ProductServiceImpl;
import org.pronsky.service.mapper.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Log4j
@NoArgsConstructor
@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARSET = "UTF-8";
    private final ConnectionUtil connectionUtil = new ConnectionUtil();
    private final ProductDAO productDAO = new ProductDAOImpl(connectionUtil);
    private final ProductCategoryDAO productCategoryDAO = new ProductCategoryDAOImpl(connectionUtil);
    private final ProductRepository productRepository = new ProductRepositoryImpl(productDAO, productCategoryDAO);
    private final Mapper mapper = Mappers.getMapper(Mapper.class);
    private final ProductService productService = new ProductServiceImpl(mapper, productRepository);
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
        try {
            String payload = getPayload(req);
            ProductDTO productDTO = objectMapper.readValue(payload, ProductDTO.class);
            productService.save(productDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Got request type PUT : " + req.getRequestURI());
        try {
            String payload = getPayload(req);
            ProductDTO productDTO = objectMapper.readValue(payload, ProductDTO.class);
            productService.save(productDTO);
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
            productService.delete(Long.parseLong(req.getParameter("id")));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void init() {
        log.info("FRONT CONTROLLER INITIALISED");
    }

    @Override
    public void destroy() {
        log.info("FRONT CONTROLLER DESTROYED");
    }

    private Long processParams(HttpServletRequest req) {
        String rawId = req.getParameter("id");
        if (rawId != null) {
            return Long.parseLong(rawId);
        } else return null;
    }

    private void getOne(Long id, HttpServletResponse resp) {
        try {
            ProductDTO productDTO = productService.getById(id);
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), productDTO);
        } catch (NumberFormatException | UnableToFindException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getAll(HttpServletResponse resp) {
        try {
            List<ProductDTO> products = productService.getAll();
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), products);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String getPayload(HttpServletRequest req) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
