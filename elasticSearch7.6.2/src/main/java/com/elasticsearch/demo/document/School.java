package com.elasticsearch.demo.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@Data
@Builder
@Document(indexName = "school_index",shards = 2,replicas = 0,refreshInterval = "1")
public class School implements Serializable {


    private static final long serialVersionUID = 3556208873148846918L;

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    @Field(type = FieldType.Keyword)
    private String name;

}
