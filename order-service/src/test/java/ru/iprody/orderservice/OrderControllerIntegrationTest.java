package ru.iprody.orderservice;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.iprody.orderservice.domain.model.OrderStatus;
import ru.iprody.orderservice.domain.repository.OrderRepository;
import ru.iprody.orderservice.web.dto.MoneyDto;
import ru.iprody.orderservice.web.dto.OrderItemDto;
import ru.iprody.orderservice.web.dto.OrderRequest;
import ru.iprody.orderservice.web.dto.ShippingAddressDto;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldPerformCrudForOrder() throws Exception {
        OrderRequest createRequest = new OrderRequest();
        createRequest.setCustomerId(101L);
        createRequest.setStatus(OrderStatus.NEW);
        createRequest.setShippingAddress(new ShippingAddressDto("Tverskaya 1", "Moscow", "125009", "RU"));
        createRequest.setItems(List.of(
                new OrderItemDto(null, "Notebook", 1, new MoneyDto(new BigDecimal("79990.00"), "RUB")),
                new OrderItemDto(null, "Mouse", 2, new MoneyDto(new BigDecimal("1990.00"), "RUB"))
        ));

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalAmount.amount").value(83970.00))
                .andReturn();

        JsonNode createdOrder = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long orderId = createdOrder.get("id").asLong();

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items", hasSize(2)));

        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerId(101L);
        updateRequest.setStatus(OrderStatus.CONFIRMED);
        updateRequest.setShippingAddress(new ShippingAddressDto("Lenina 10", "Moscow", "101000", "RU"));
        updateRequest.setItems(List.of(
                new OrderItemDto(null, "Notebook", 1, new MoneyDto(new BigDecimal("79990.00"), "RUB"))
        ));

        mockMvc.perform(put("/api/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.shippingAddress.street").value("Lenina 10"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.totalAmount.amount").value(79990.00));

        mockMvc.perform(delete("/api/orders/{id}", orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }
}
