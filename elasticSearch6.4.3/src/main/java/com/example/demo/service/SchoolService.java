package com.example.demo.service;

import com.example.demo.document.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * created by zc  2020/11/18 13:52
 */
public interface SchoolService {

    School getById(String id);

    Page<School> listSchool(PageRequest pageRequest);

    void removeSchoolById(String id);

    School save(School school);



}
