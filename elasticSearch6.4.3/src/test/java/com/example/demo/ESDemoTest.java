package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.document.*;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


/**
 * created by zc  2020/11/18 14:54
 */

@Slf4j
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


    /*********************************   elasticsearchTemplate    *********************************************************/

    @Test
    public void createIndex() throws ParseException {
        Student student = Student.builder()
                .id("1")
                .address("火星")
                .name("小黑")
                .sex("男")
                .age(11)
                .birthDay(new Date())
                .evaluation("快乐男孩")
                .hobby(new String[]{"打球，听音乐", "阅读"})
                .build();
        Student student1 = studentRepository.save(student);
        System.out.println(student1);

        long start_time = System.currentTimeMillis();
        School build = School.builder()
                .address("武汉")
                .id("1")
                .name("武汉大学")
                .property("公立大学")
                .stuNum(10000)
                .openDate(new Date())
                .createTime(new Date())
                .build();
        School school = schoolService.save(build);
        System.out.println(school);
        long ent_time = System.currentTimeMillis();
        System.out.println(ent_time - start_time);
    }


    @Test
    public void createIndexTwo() {
        Student student = Student.builder()
                .id("2")
                .address("月亮")
                .name("小白")
                .sex("女")
                .age(11)
                .birthDay(new Date())
                .evaluation("carrot")
                .hobby(new String[]{"打球，听音乐", "阅读"})
                .build();

        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(student);
        String index = elasticsearchTemplate.index(indexQuery);
    }

    @Test
    public void testBulkIndex() {
        long s_time = System.currentTimeMillis();
        List<IndexQuery> persons = createPerson();
        elasticsearchTemplate.bulkIndex(persons);
        elasticsearchTemplate.refresh(Person.class);
        long e_time = System.currentTimeMillis();
        log.info("time cost {}", e_time - s_time); //  1640
    }

    private List<IndexQuery> createPerson() {

        final List<Car> cars = new ArrayList<>();
        final Car saturn = new Car();
        saturn.setName("Saturn");
        saturn.setModel("SL");
        final Car subaru = new Car();
        subaru.setName("Subaru");
        subaru.setModel("Imprezza");
        final Car ford = new Car();
        ford.setName("Ford");
        ford.setModel("Focus");
        cars.add(saturn);
        cars.add(subaru);
        cars.add(ford);

        List<Book> books = new ArrayList<>();
        String[] bs = {"语文", "数学", "外语", "音乐", "英语", "化学", "美术", "地理", "历史", "物理"};
        String[] name = {"张三", "李思", "王武", "马六", "钟琪", "重八"};
        for (int i = 0; i < bs.length; i++) {
            int id = new Random().nextInt(bs.length);
            int r = new Random().nextInt(name.length);
            Book book = new Book();
            book.setId(String.valueOf(id));
            book.setName(bs[id]);
            book.setDescription("这真是一本好书呀！！！");
            book.setAuthor(new Author(String.valueOf(id), name[r]));
            books.add(book);
        }

        String[] firstName = {"黄", "张", "郭", "马", "田", "杨", "风", "云", "李", "川", "宋", "吴", "余"};
        String[] lastName = {"建国", "建军", "荣峰", "强", "超", "忠君", "爱国", "寻峰", "勇德", "飞翔", "成俊", "买提",
                "刚", "清扬", "药师", "浩天", "志祥", "磊", "旺财", "笑书"};

        final List<IndexQuery> indexQueries = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            final Person foo = new Person();
            String rName = firstName[new Random().nextInt(firstName.length)]
                    + lastName[new Random().nextInt(lastName.length)];
            foo.setName(rName);
            foo.setId(String.valueOf(i));
            foo.setCar(cars);
            foo.setBooks(books);
            final IndexQuery indexQuery1 = new IndexQuery();
            indexQuery1.setId(foo.getId());
            indexQuery1.setObject(foo);
            indexQueries.add(indexQuery1);
        }

        return indexQueries;
    }


    @Test
    public void testUpdate() {
        Person person = new Person();
        person.setId(String.valueOf(437));
        person.setName("马超");
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(JSON.toJSONString(person), XContentType.JSON);
        UpdateQuery build = new UpdateQueryBuilder()
                .withId("437")
                .withClass(Person.class)
                .withIndexRequest(indexRequest)
                .build();
        elasticsearchTemplate.update(build);
    }

    @Test
    public void getCount_CriteriaQuery() {
        Criteria criteria = new Criteria("name.keyword");
        criteria.contains("张建军");
        Criteria criteria2 = new Criteria("books.author.name");
        criteria.contains("武");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        criteriaQuery.addCriteria(criteria2);
        long count = elasticsearchTemplate.count(criteriaQuery, Person.class);

        System.out.println(count);
    }

    @Test
    public void testQuery_getQuery() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1");
        School school = elasticsearchTemplate.queryForObject(getQuery, School.class);
        System.out.println(JSONObject.toJSON(school).toString());
    }

    @Test
    public void testQuery_NativeSearchQuery() {
        TermQueryBuilder termsQueryBuilder = QueryBuilders
                .termQuery("name.keyword", "马超");
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders
                .fuzzyQuery("books.author.name", "八")
                .fuzziness(Fuzziness.ONE);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(termsQueryBuilder);
        boolQueryBuilder.filter(fuzzyQueryBuilder);

//        boolQueryBuilder.should(QueryBuilders.termQuery("name.keyword","sdf")).minimumShouldMatch(1); // 最少匹配一个
//        boolQueryBuilder.should(QueryBuilders.termQuery("name.keyword","sdf").boost(1)); // boost 权重（默认1越大越重要）


        // 返回指定列
        FetchSourceFilterBuilder sourceFilter = new FetchSourceFilterBuilder();
        sourceFilter.withIncludes("books.author", "name");

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSourceFilter(sourceFilter.build())
                .build();

        List<Person> people = elasticsearchTemplate.queryForList(build, Person.class);

        log.info("query DSL : {}", build.getQuery().toString());

        if (null != people) {
            people.forEach(person -> {
                System.out.println(JSON.toJSON(person).toString());
            });
        }
    }

    @Test
    public void testQuery_StringQuery() {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termsQuery("name.keyword", "马超"));
        StringQuery stringQuery = new StringQuery(boolQueryBuilder.toString());
        stringQuery.setPageable(PageRequest.of(0, 10));

        FetchSourceFilterBuilder sourceFilter = new FetchSourceFilterBuilder();
        sourceFilter.withIncludes("name", "id");
        stringQuery.addSourceFilter(sourceFilter.build());

        List<Person> people = elasticsearchTemplate.queryForList(stringQuery, Person.class);

        Stream.of(people).forEach(p -> System.out.println(JSON.toJSON(p).toString()));
    }

    // ES-NODE RoutingKey
    @Test
    public void testQuery_alias() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword", "湘北中学");
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQueryBuilder)
                .withIndices("ss_alias")
                .build();
//        List<School> schools = elasticsearchTemplate.queryForList(searchQuery, School.class);

        Page<School> search = schoolRepository.search(searchQuery);

        System.out.println(searchQuery.getQuery().toString());
        Stream.of(search.getContent()).forEach(s -> System.out.println(JSON.toJSONString(s)));
    }

    @Test
    public void testUpdate_alias() {
        School school = new School();
        school.setId("2");
        school.setCreateTime(new Date());

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.doc(JSON.toJSONString(school), XContentType.JSON);
        updateRequest.id(school.getId());
        updateRequest.type("_doc");
        updateRequest.index("middle_school_alias");


        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withUpdateRequest(updateRequest)
                .withId(updateRequest.id())
                .withType(updateRequest.type())
                .withIndexName(updateRequest.index())
                .build();

        UpdateResponse update = elasticsearchTemplate.update(updateQuery);
        System.out.println(JSON.toJSONString(update));
    }


    @Test
    public void testSave_alis(){
        School school = new School();
        school.setId("3");
        school.setAddress("洗马");



//        IndexQuery indexQuery = new IndexQuery();
//        indexQuery.setIndexName("middle_school_alias");
//        indexQuery.setObject(school);
//        indexQuery.setId(school.getId());
//        String index = elasticsearchTemplate.index(indexQuery);


//        schoolRepository.save(school);

    }


    @Test
    public void delByQuery(){
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termQuery("name.keyword","洗马三中"));
        deleteQuery.setIndex("middle_school_alias");
        deleteQuery.setType("_doc");
        elasticsearchTemplate.delete(deleteQuery);

    }
}
