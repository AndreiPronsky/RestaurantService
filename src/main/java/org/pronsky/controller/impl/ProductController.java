package org.pronsky.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pronsky.controller.Controller;
import org.pronsky.service.ProductService;
import org.pronsky.service.dto.ProductCategoryDTO;
import org.pronsky.service.dto.ProductDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ProductController implements Controller {

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARSET = "UTF-8";
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("ProductController.doGet");
        Long id = processParams(req);
        if (id == null) {
            getAll(resp);
        } else {
            getOne(id, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("ProductController.doPost");
        try (BufferedReader reader = req.getReader()) {
            String payload = getPayload(reader);
            JsonNode root = objectMapper.readTree(payload);
            JsonNode products = root.get("products");
            if (products.isArray()) {
                for (JsonNode product : products) {
                    ProductDTO productDTO = deserialize(product);
                    productService.save(productDTO);
                }
            }
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        log.info("ProductController.doPut");
        try {
            JsonNode node = objectMapper.readTree(req.getReader());
            ProductDTO productDTO = deserialize(node);
            ProductDTO updated = productService.save(productDTO);
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), updated);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        log.info("ProductController.doDelete");
        try {
            productService.delete(Long.parseLong(req.getParameter("id")));
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
            ProductDTO productDTO = productService.getById(id);
            resp.setContentType(CONTENT_TYPE);
            resp.setCharacterEncoding(CHARSET);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), productDTO);
        } catch (NumberFormatException e) {
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

    private ProductDTO deserialize(JsonNode node) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(node.get("id").asLong());
        productDTO.setName(node.get("name").asText());
        productDTO.setPrice(BigDecimal.valueOf(node.get("price").asDouble()));
        productDTO.setQuantity(node.get("quantity").asInt());
        productDTO.setAvailable(node.get("available").asBoolean());
        List<ProductCategoryDTO> categories = new ArrayList<>();
        deserializeAndSetCategories(node, productDTO, categories);
        return productDTO;
    }

    private void deserializeAndSetCategories(JsonNode node, ProductDTO productDTO, List<ProductCategoryDTO> categories) {
        for (JsonNode category : node.get("categories")) {
            ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
            productCategoryDTO.setName(category.get("name").asText());
            Set<ProductCategoryDTO.CategoryType> categoryTypes = new HashSet<>();
            for (JsonNode categoryType : node.get("types")) {
                categoryTypes.add(ProductCategoryDTO.CategoryType.valueOf(categoryType.asText().toUpperCase()));
            }
            productCategoryDTO.setTypes(categoryTypes);
        }
        productDTO.setCategories(categories);
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
