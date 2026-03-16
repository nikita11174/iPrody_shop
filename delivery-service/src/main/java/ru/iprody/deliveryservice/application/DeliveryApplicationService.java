package ru.iprody.deliveryservice.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iprody.deliveryservice.common.ResourceNotFoundException;
import ru.iprody.deliveryservice.domain.model.Delivery;
import ru.iprody.deliveryservice.domain.model.DeliveryAddress;
import ru.iprody.deliveryservice.domain.model.TimeWindow;
import ru.iprody.deliveryservice.domain.repository.DeliveryRepository;
import ru.iprody.deliveryservice.web.dto.DeliveryAddressDto;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryResponse;
import ru.iprody.deliveryservice.web.dto.TimeWindowDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryApplicationService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse create(DeliveryRequest request) {
        Delivery delivery = new Delivery(
                request.getOrderId(),
                request.getStatus(),
                toDeliveryAddress(request.getDeliveryAddress()),
                request.getDeliveryDate(),
                toTimeWindow(request.getTimeWindow()),
                request.getTrackingNumber()
        );
        return toResponse(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponse> getAll() {
        return deliveryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DeliveryResponse getById(Long id) {
        return toResponse(getDelivery(id));
    }

    @Transactional
    public DeliveryResponse update(Long id, DeliveryRequest request) {
        Delivery delivery = getDelivery(id);
        delivery.update(
                request.getOrderId(),
                request.getStatus(),
                toDeliveryAddress(request.getDeliveryAddress()),
                request.getDeliveryDate(),
                toTimeWindow(request.getTimeWindow()),
                request.getTrackingNumber()
        );
        return toResponse(delivery);
    }

    @Transactional
    public void delete(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery with id " + id + " was not found");
        }
        deliveryRepository.deleteById(id);
    }

    private Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " was not found"));
    }

    private DeliveryAddress toDeliveryAddress(DeliveryAddressDto address) {
        if (address == null) {
            throw new IllegalArgumentException("Delivery address must be provided");
        }
        return new DeliveryAddress(
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry()
        );
    }

    private TimeWindow toTimeWindow(TimeWindowDto window) {
        if (window == null) {
            throw new IllegalArgumentException("Time window must be provided");
        }
        return new TimeWindow(window.getStartTime(), window.getEndTime());
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                new DeliveryAddressDto(
                        delivery.getDeliveryAddress().getStreet(),
                        delivery.getDeliveryAddress().getCity(),
                        delivery.getDeliveryAddress().getPostalCode(),
                        delivery.getDeliveryAddress().getCountry()
                ),
                delivery.getDeliveryDate(),
                new TimeWindowDto(
                        delivery.getTimeWindow().getStartTime(),
                        delivery.getTimeWindow().getEndTime()
                ),
                delivery.getTrackingNumber()
        );
    }
}
