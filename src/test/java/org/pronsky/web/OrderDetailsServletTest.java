package org.pronsky.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductDTO;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderDetailsServletTest {

    private static final Long ID = 10L;
    private static final Long ANOTHER_ID = 5L;
    private static final String ID_PARAM = "id";
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARSET = "UTF-8";
    private static final String PAYLOAD = """
                {
                "id": 10,
                "orderStatus": "OPEN",
                "products": [
                    {
                        "id": 4,
                        "name": "Beef short ribs",
                        "price": 4.8,
                        "quantity": 3,
                        "available": true,
                        "productCategories": [
                            {
                                "id": 2,
                                "name": "MEAT",
                                "types": [
                                    "FREEZER_STORAGE",
                                    "PERISHABLE"
                                ]
                            }
                        ]
                    },
                    {
                        "id": 11,
                        "name": "Milk",
                        "price": 2,
                        "quantity": 6,
                        "available": true,
                        "productCategories": [
                            {
                                "id": 3,
                                "name": "DAIRY",
                                "types": [
                                    "FRIDGE_STORAGE",
                                    "PERISHABLE"
                                ]
                            }
                        ]
                    },
                    {
                        "id": 26,
                        "name": "Banana",
                        "price": 2,
                        "quantity": 3,
                        "available": true,
                        "productCategories": [
                            {
                                "id": 7,
                                "name": "FRUITS",
                                "types": [
                                    "FRIDGE_STORAGE"
                                ]
                            }
                        ]
                    }
                ],
                "totalAmount": 79.6
            }
            """;

    private static ProductDTO productDTO;
    private static ProductDTO anotherProductDTO;
    private static List<ProductDTO> productDTOList;
    private static OrderDetailsDTO detailsDTO;
    private static OrderDetailsDTO anotherDetailsDTO;
    private static List<OrderDetailsDTO> detailsDTOList;

    @Mock
    private OrderDetailsService orderDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private OrderDetailsServlet orderDetailsServlet;

    @BeforeAll
    static void setUpBeforeClass() {
        productDTO = new ProductDTO();
        productDTO.setId(ID);
        productDTO.setName("Test product");
        productDTO.setPrice(BigDecimal.valueOf(75.0));
        productDTO.setQuantity(10);
        productDTO.setAvailable(true);

        anotherProductDTO = new ProductDTO();
        anotherProductDTO.setId(ANOTHER_ID);
        anotherProductDTO.setName("Another product");
        anotherProductDTO.setPrice(BigDecimal.valueOf(5.0));
        anotherProductDTO.setQuantity(10);
        anotherProductDTO.setAvailable(true);

        productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);
        productDTOList.add(anotherProductDTO);

        detailsDTO = new OrderDetailsDTO();
        detailsDTO.setId(ID);
        detailsDTO.setOrderStatus(OrderDetailsDTO.OrderStatus.COMPLETED);
        detailsDTO.setTotalAmount(BigDecimal.valueOf(124.5));
        detailsDTO.setProducts(productDTOList);

        anotherDetailsDTO = new OrderDetailsDTO();
        anotherDetailsDTO.setId(ANOTHER_ID);
        anotherDetailsDTO.setOrderStatus(OrderDetailsDTO.OrderStatus.OPEN);
        anotherDetailsDTO.setTotalAmount(BigDecimal.valueOf(12.5));
        anotherDetailsDTO.setProducts(productDTOList);

        detailsDTOList = new ArrayList<>();
        detailsDTOList.add(detailsDTO);
        detailsDTOList.add(anotherDetailsDTO);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDoGetWithId() throws IOException {
        when(request.getParameter(ID_PARAM)).thenReturn(String.valueOf(ID));
        when(orderDetailsService.getById(ID)).thenReturn(detailsDTO);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        objectMapper.writeValue(printWriter, detailsDTO);
        orderDetailsServlet.doGet(request, response);
        OrderDetailsDTO actual = objectMapper.readValue(stringWriter.toString(), OrderDetailsDTO.class);
        assertEquals(detailsDTO, actual);
    }

    @Test
    void testDoGetAll() throws IOException {
        when(request.getParameter(ID_PARAM)).thenReturn(null);
        when(orderDetailsService.getAll()).thenReturn(detailsDTOList);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        objectMapper.writeValue(printWriter, detailsDTOList);
        orderDetailsServlet.doGet(request, response);
        verify(response).setContentType(CONTENT_TYPE);
        verify(response).setCharacterEncoding(CHARSET);
        verify(response).setStatus(HttpServletResponse.SC_OK);

    }

    @Test
    @Disabled
    void testDoPost() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(PAYLOAD));
        when(request.getReader()).thenReturn(reader);
        when(objectMapper.readValue(PAYLOAD, OrderDetailsDTO.class)).thenReturn(detailsDTO);
        when(orderDetailsService.save(detailsDTO)).thenReturn(detailsDTO);
        orderDetailsServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
        verify(orderDetailsService).save(detailsDTO);
    }

    @Test
    @Disabled
    void testDoPut() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(PAYLOAD));
        when(request.getReader()).thenReturn(reader);
        JsonNode node = mock(JsonNode.class);
        when(objectMapper.readTree(reader)).thenReturn(node);
        when(objectMapper.treeToValue(node, OrderDetailsDTO.class)).thenReturn(detailsDTO);
        when(orderDetailsService.save(detailsDTO)).thenReturn(detailsDTO);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        orderDetailsServlet.doPut(request, response);
        verify(response).setContentType(CONTENT_TYPE);
        verify(response).setCharacterEncoding(CHARSET);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValue(printWriter, detailsDTO);
    }

    @Test
    void testDoDelete() {
        when(request.getParameter(ID_PARAM)).thenReturn(String.valueOf(ID));
        orderDetailsServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
