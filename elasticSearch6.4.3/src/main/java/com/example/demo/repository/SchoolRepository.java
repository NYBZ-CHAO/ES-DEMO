package com.example.demo.repository;

import com.example.demo.document.School;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * created by zc  2020/11/18 13:45
 */
public interface SchoolRepository extends ElasticsearchRepository<School,String> {


}
