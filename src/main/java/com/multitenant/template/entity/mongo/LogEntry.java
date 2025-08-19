package com.multitenant.template.entity.mongo;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "logs")
public class LogEntry {
    @Id
    private String id;
    private String message;
    private Long timestamp;
}
