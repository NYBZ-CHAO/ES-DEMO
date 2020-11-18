package com.elasticsearch.demo.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;


@Data
@Builder
@Document(indexName = "teacher_index")
@Mapping(mappingPath = "/json/mappings/teacher_mappings.json")
@Setting(settingPath = "/json/settings/teacher_settings.json")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 3556208873148846918L;

    @Id
    private Long tId;
    private String address;
    private String name;


}
