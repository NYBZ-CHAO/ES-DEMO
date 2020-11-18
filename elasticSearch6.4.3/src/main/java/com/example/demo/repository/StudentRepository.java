package com.example.demo.repository;

import com.example.demo.document.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * created by zc  2020/11/18 17:58
 */
public interface StudentRepository extends ElasticsearchRepository<Student,String> {

}
