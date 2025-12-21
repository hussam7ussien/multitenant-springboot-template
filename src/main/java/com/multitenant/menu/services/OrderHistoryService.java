package com.multitenant.menu.services;

import com.multitenant.menu.dto.OrderHistoryGroupDTO;
import com.multitenant.menu.entity.sql.OrderEntity;
import com.multitenant.menu.repository.sql.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter GROUP_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<OrderHistoryGroupDTO> getOrderHistoryByDate(Long userId, Pageable pageable) {
        // Fetch only this user's orders, ordered by creation date descending
        Page<OrderEntity> ordersPage = orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId, pageable);

        // Group orders by date (yyyy-MM-dd)
        Map<String, List<OrderEntity>> grouped = ordersPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().format(GROUP_DATE),
                        LinkedHashMap::new, Collectors.toList()
                ));
        
        List<OrderHistoryGroupDTO> groups = grouped.entrySet().stream()
                .map(entry -> new OrderHistoryGroupDTO(
                        entry.getKey(),
                        entry.getValue().stream().map(order ->
                                new OrderHistoryGroupDTO.OrderSummaryDTO(order.getOrderCode(), order.getStatus())
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList());
        
        // Custom pagination of groups list if grouping reduced total
        int totalGroups = groups.size();
        int offset = (int) pageable.getOffset();
        int limit = Math.min(offset + pageable.getPageSize(), totalGroups);
        List<OrderHistoryGroupDTO> pagedGroups = (offset > limit) ? Collections.emptyList() : groups.subList(offset, limit);
        return new PageImpl<>(pagedGroups, pageable, totalGroups);
    }
}


