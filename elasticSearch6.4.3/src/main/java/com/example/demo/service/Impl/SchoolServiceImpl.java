package com.example.demo.service.Impl;

import com.example.demo.document.School;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.service.SchoolService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * created by zc  2020/11/18 13:54
 */
@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public School getById(String id) {
        Optional<School> optional = schoolRepository.findById(id);
        return optional.orElseGet(() -> null);
    }

    @Override
    public Page<School> listSchool(PageRequest pageRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("name", "武汉大学"));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder);
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.withPageable(pageRequest).build();
        Page<School> search = schoolRepository.search(searchQuery);
        return search;
    }

    @Override
    public void removeSchoolById(String id) {
        schoolRepository.deleteById(id);
    }

    @Override
    public School save(School school) {
        return schoolRepository.save(school);
    }
}
