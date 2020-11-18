package com.example.demo;

import com.example.demo.document.School;
import com.example.demo.document.Student;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.SchoolService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;

/**
 * created by zc  2020/11/18 14:54
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class ESDemoTest {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testOne(){
        long start_time = System.currentTimeMillis();
        School build = School.builder().address("武汉").id("1").name("武汉大学").property("公立大学").stuNum(10000).build();
        School save = schoolService.save(build);
        long ent_time = System.currentTimeMillis();
        System.out.println(ent_time-start_time);
    }

    @Test
    public void testTwo(){
        School school = schoolService.getById("1");
        System.out.println(school.toString());
    }


    /*********************************   elasticsearchTemplate    *********************************************************/

    @Test
    public void createIndex() throws ParseException {
        Student student = Student.builder()
                .id("1")
                .address("中国")
                .name("小明")
                .sex("男")
                .age(11)
                .birthDay(new Date())
                .evaluation("三号学生")
                .hobby(new String[]{"打球，听音乐", "阅读"})
                .build();

        Student save = studentRepository.save(student);
//        IndexQuery indexQuery = new IndexQueryBuilder()
//                .withId(student.getId())
//                .withObject(student)
//                .withType("_doc")
//                .build();
//        String index = elasticsearchTemplate.index(indexQuery);


    }

//    @Test
//    public void del

}
