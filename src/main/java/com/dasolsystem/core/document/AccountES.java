package com.dasolsystem.core.document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "logs_index")
public class AccountES {
    @Id
    private Long id;

    private String name;

    private Long balance;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String message;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_optional_time_nanos)
    private OffsetDateTime updated_at;
}
