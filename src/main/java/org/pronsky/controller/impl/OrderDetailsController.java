package org.pronsky.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pronsky.controller.Controller;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderDetailsController implements Controller {

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARSET = "UTF-8";
    private final OrderDetailsService orderDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("OrderDetailsController.doGet");
        Long id = processParams(req);
        if (id == null) {
            getAll(resp);
        } else {
            getOne(id, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("OrderDetailsController.doPost");
        try (BufferedReader reader = req.getReader()) {
            String payload = getPayload(reader);
            JsonNode root = objectMapper.readTree(payload);
            JsonNode orderDetails = root.get("order_details");
            if (orderDetails.isArray()) {
                for (JsonNode orderDetail : orderDetails) {
                    OrderDetailsDTO orderDetailsDTO = deserialize(orderDetail);
                    orderDetailsService.save(orderDetailsDTO);
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
        log.info("OrderDetailsController.doPut");
        try {
            JsonNode node = objectMapper.readTree(req.getReader());
            OrderDetailsDTO orderDetailsDTO = deserialize(node);
            OrderDetailsDTO updated = orderDetailsService.save(orderDetailsDTO);
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
        log.info("OrderDetailsController.doDelete");
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
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private OrderDetailsDTO deserialize(JsonNode node) {
        OrderDetailsDTO orderDetails = new OrderDetailsDTO();
        orderDetails.setId(node.get("id").asLong());
        orderDetails.setOrderStatus(OrderDetailsDTO.OrderStatus.valueOf(node.get("order_status").asText().toUpperCase()));
        orderDetails.setTotalAmount(BigDecimal.valueOf(node.get("total_amount").asDouble()));
        List<ProductDTO> products = new ArrayList<>();
        deserializeAndSetProducts(node, products, orderDetails);
        return orderDetails;
    }

    private void deserializeAndSetProducts(JsonNode node, List<ProductDTO> products, OrderDetailsDTO orderDetailsDTO) {
        for (JsonNode product : node.get("products")) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.get("id").asLong());
            productDTO.setName(product.get("name").asText());
            productDTO.setPrice(BigDecimal.valueOf(product.get("price").asDouble()));
            productDTO.setQuantity(product.get("quantity").asInt());
            productDTO.setAvailable(product.get("available").asBoolean());
            products.add(productDTO);
        }
        orderDetailsDTO.setProducts(products);
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
