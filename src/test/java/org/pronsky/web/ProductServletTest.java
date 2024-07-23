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
import org.pronsky.service.ProductService;
import org.pronsky.service.dto.ProductDTO;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServletTest {

    private static final Long ID = 3L;
    private static final Long ANOTHER_ID = 5L;
    private static final String ID_PARAM = "id";
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARSET = "UTF-8";
    private static final String PAYLOAD = """
                        {
                        "id": 130,
                        "name": "Test create 6",
                        "price": 30,
                        "quantity": 3,
                        "available": false,
                        "productCategories": [
                {
                    "id": 26,
                        "name": "MEAT",
                        "types": [
                    "FREEZER_STORAGE",
                            "PERISHABLE"
                        ]
                }
                ]
            }
            """;

    private static ProductDTO productDTO;
    private static ProductDTO anotherProductDTO;
    private static List<ProductDTO> productDTOList;

    @Mock
    private ProductService productService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ProductServlet productServlet;

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
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doGetWithId() throws IOException {
        when(request.getParameter(ID_PARAM)).thenReturn(String.valueOf(ID));
        when(productService.getById(ID)).thenReturn(productDTO);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper.writeValue(writer, productDTO);
        productServlet.doGet(request, response);
        ProductDTO actualProductDTO = objectMapper.readValue(stringWriter.toString(), ProductDTO.class);
        assertEquals(productDTO, actualProductDTO);
    }


    @Test
    void testDoGetAll() throws IOException {
        when(request.getParameter(ID_PARAM)).thenReturn(null);
        when(productService.getAll()).thenReturn(productDTOList);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper.writeValue(writer, productDTOList);
        productServlet.doGet(request, response);
        verify(response).setContentType(CONTENT_TYPE);
        verify(response).setCharacterEncoding(CHARSET);
        verify(response).setStatus(HttpServletResponse.SC_OK);

    }

    @Test
    @Disabled
    void testDoPost() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(PAYLOAD));
        when(request.getReader()).thenReturn(reader);
        objectMapper.readValue(PAYLOAD, ProductDTO.class);
        when(productService.save(productDTO)).thenReturn(productDTO);
        productServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(productService).save(productDTO);
    }

    @Test
    @Disabled
    void testDoPut() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(PAYLOAD));
        when(request.getReader()).thenReturn(reader);
        JsonNode node = mock(JsonNode.class);
        when(objectMapper.readTree(reader)).thenReturn(node);
        when(objectMapper.treeToValue(node, ProductDTO.class)).thenReturn(productDTO);
        when(productService.save(productDTO)).thenReturn(productDTO);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        productServlet.doPut(request, response);
        verify(response).setContentType(CONTENT_TYPE);
        verify(response).setCharacterEncoding(CHARSET);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValue(printWriter, productDTO);
    }

    @Test
    void testDoDelete() {
        when(request.getParameter(ID_PARAM)).thenReturn(String.valueOf(ID));
        productServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
