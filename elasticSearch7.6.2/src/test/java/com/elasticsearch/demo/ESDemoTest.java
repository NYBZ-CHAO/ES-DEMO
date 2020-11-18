package com.elasticsearch.demo;


import com.elasticsearch.demo.document.Student;
import com.elasticsearch.demo.document.Teacher;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ESDemoTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;



    @Test
    public void esCreateIndex() throws IOException {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Teacher.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
        Document mapping = indexOperations.createMapping(Teacher.class);
        indexOperations.create(mapping);
        indexOperations.refresh();
    }


    /**
     * 新增或者修改 文档信息
     */
    @Test
    public void esCreateDocument() {
        Teacher build = Teacher.builder().tId(1L).name("丽丽").address("北京紫禁城").build();
        elasticsearchRestTemplate.save(build);
    }

    /**
     * 批量添加文档
     */
    @Test
    public void bulkIndex() {
        IndexCoordinates school_index = IndexCoordinates.of("student_index");
        Student stu1 = Student.builder()
                .id("1")
                .age(11)
                .name("张小强")
                .birthDay(new Date())
                .address("上海浦东")
                .sex("男")
                .evaluation("他是一个勇敢坚强，乐于助人，锄强扶弱，为人正直的好学生！")
                .build();
        Student stu2 = Student.builder()
                .id("2")
                .age(10)
                .name("小花")
                .birthDay(new Date())
                .address("北京天安门")
                .sex("女")
                .evaluation("她是一个善良可爱，平易近人，聪明好学，品学兼优的好学生！")
                .build();
        Student stu3 = Student.builder()
                .id("3")
                .age(13)
                .name("小黑")
                .birthDay(new Date())
                .address("武汉汉正街")
                .sex("男")
                .evaluation("他是一个活泼好动，仗义疏财，乐善好施，打抱不平，痞气十足的好学生！")
                .build();
        Student stu4 = Student.builder()
                .id("4")
                .age(15)
                .name("锦小明")
                .birthDay(new Date())
                .address("云南大理")
                .sex("男")
                .evaluation("他是一个阳光开朗，潇洒不羁，仗剑天涯，武功卓著，拈花惹草好学生！")
                .build();
        Student stu5 = Student.builder()
                .id("5")
                .age(16)
                .name("马天鹏")
                .birthDay(new Date())
                .address("新疆乌鲁木齐")
                .sex("男")
                .evaluation("他是一个高大伟岸，淳朴善良，正直果敢，一若千金好学生！")
                .build();
        LinkedList<IndexQuery> indexQueries = new LinkedList<>();
        indexQueries.add(new IndexQueryBuilder().withId(stu1.getId()).withObject(stu1).build());
        indexQueries.add(new IndexQueryBuilder().withId(stu2.getId()).withObject(stu2).build());
        indexQueries.add(new IndexQueryBuilder().withId(stu3.getId()).withObject(stu3).build());
        indexQueries.add(new IndexQueryBuilder().withId(stu4.getId()).withObject(stu4).build());
        indexQueries.add(new IndexQueryBuilder().withId(stu5.getId()).withObject(stu5).build());

        elasticsearchRestTemplate.bulkIndex(indexQueries, school_index);
        elasticsearchRestTemplate.indexOps(school_index).refresh();

    }

    /**
     * 批量更新
     */
    @Test
    public void bulkUpdate() throws ParseException {
        IndexCoordinates student_index = IndexCoordinates.of("student_index");
//        Student student = elasticsearchRestTemplate.get("1", Student.class);
//        Document document = Document.create();
//        document.append("name","张小明");
//        UpdateQuery build = UpdateQuery.builder(student.getId())
//                .withDocument(document)
//                .build();
//        elasticsearchRestTemplate.update(build,student_index);

        System.out.println("-----------------bulkUpdate start---------------------");

//        NativeSearchQuery query = new NativeSearchQueryBuilder().withIds(Collections.singleton(student.getId())).build();
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        SearchHits<Student> students = elasticsearchRestTemplate.search(query, Student.class, student_index);
        //  multiGet  根据id查询
//        List<Student> students = elasticsearchRestTemplate.multiGet(query, Student.class, student_index);
        LinkedList<UpdateQuery> updateQueries = new LinkedList<>();

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-08-08 08:08:08");
        students.getSearchHits().forEach(s -> {
            Document d = Document.create();
            d.append("birthDay", date);
//            d.append("age",s.getContent().getAge()+100);
            UpdateQuery b = UpdateQuery.builder(s.getId())
                    .withDocument(d)
                    .build();
            updateQueries.add(b);

        });
        elasticsearchRestTemplate.bulkUpdate(updateQueries, student_index);
        elasticsearchRestTemplate.indexOps(student_index).refresh();

        System.out.println("-----------------bulkUpdate end---------------------");
    }


    @Test
    public void countTest() {
        CriteriaQuery criteriaQueryName = new CriteriaQuery(new Criteria("name").contains("小强"));
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        long count = elasticsearchRestTemplate.count(criteriaQueryName, IndexCoordinates.of("student_index"));
        System.out.println(count);
    }


    @Test
    public void query() {
        IndexCoordinates student_index = IndexCoordinates.of("student_index");
        IndexCoordinates teacher_index = IndexCoordinates.of("teacher_index");



        /**
         * QueryBuilders 一些用法
         *
         * 1.QueryBuilders.termQuery("hotelName","hotel")   分词精确查询查询hotelName 分词后包含 hotel的term的文档
         *
         * 2.QueryBuilders.termsQuery("hotelName","hotel","test")
         *      terms Query 多term查询，查询hotelName 包含 hotel 或test 中的任何一个或多个的文档
         *
         * 3.QueryBuilders.rangeQuery("hotelNo").gt("10143262306").lt("101432623062055348221")
         *      range query范围查询 查询hotelNo
         *
         * 4.QueryBuilders.existsQuery("address") 查询字段address 不为null的数据
         *
         * 5.QueryBuilders.missingQuery("accountGuid")
         *      等于 -> QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("accountGuid"));
         *      返回 没有字段或值为null或没有值的文档
         *
         * 6.QueryBuilders.prefixQuery("hotelName","花园") 匹配分词前缀 如果字段没分词，就匹配整个字段前缀
         *
         * 7.QueryBuilders.wildcardQuery("channelCode","ctr*")
         *      & QueryBuilders.wildcardQuery("channelCode","ctr?")
         *      通配符查询，支持* 任意字符串；？任意一个字符
         *
         * 8.QueryBuilders.fuzzyQuery("hotelName", "tel").fuzziness(Fuzziness.ONE)
         *      检索的term前后增加或减少n个单词的匹配查询 此时检索增减一个单词的情况 （te,el tel前或后+-一个字母）
         *
         * 9.QueryBuilders.idsQuery().addIds("","") 根据ID查询
         */


        /**
         * termQuery   字段没有分词注意带上 name.keyword
         */
        // ********************************** query1 ********************************************
//        NativeSearchQuery query1 = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.termQuery("name.keyword", "小强")).build();

        /**
         * matchQuery  会分词
         */
        // ********************************** query2 ********************************************
//        NativeSearchQuery query2 = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("name", "小强").operator(Operator.AND)).build();

        /**
         * CriteriaQuery 字段没有分词注意带上 name.keyword
         */
        // ********************************** query3 ********************************************
//        CriteriaQuery query3 = new CriteriaQuery(new Criteria("address.keyword").contains("广州天河"));

        /**
         * StringQuery 字段没有分词注意带上 name.keyword
         */
        // ********************************** query4 ********************************************
//        StringQuery query4 = new StringQuery(termQuery("name.keyword", "小强").toString());

        /**
         * withPageable 分业
         */
        // ********************************** query5 ********************************************
//        NativeSearchQuery query5 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withPageable(PageRequest.of(0, 2)).build();

        /**
         * wildcardQuery 模糊查询
         */
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html#bool-min-should-match
        // ********************************** query6 ********************************************
//        NativeSearchQuery query6 = new NativeSearchQueryBuilder()
//                .withQuery(
//                        boolQuery()
//                                .must(wildcardQuery("name", "*小*"))
//                                .should(wildcardQuery("name", "*强*"))
//                                .minimumShouldMatch(1) // 至少有一个字句匹配
//                )
//                .withMinScore(2.0F).build();

        /**
         * withSort  排序字段 类型 需要 keyword
         */
        // withTrackScores 计算最高分
        // ********************************** query7 ********************************************
//        NativeSearchQuery query7 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withSort(SortBuilders.fieldSort("name.keyword"))
//                .withTrackScores(true)
//                .build();

        //  ？？？？？？？
        // ********************************** query8 ********************************************
//        Map<String, Object> params = new HashMap<>();
//        params.put("factor", 2);
//        NativeSearchQuery query8 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withScriptField(
//                    new ScriptField("scriptedRate",
//                            new Script(ScriptType.INLINE,
//                                    "expression",
//                                    "doc['rate'] * factor",
//                                    params)
//                    )
//                )
//                .build();


        /**
         * withPageable  分页 排序
         */
        // ********************************** query9 ********************************************
//        NativeSearchQuery query9 = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("evaluation", "一"))
//                .withPageable(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("_score"))))
//                .build();


        /**
         * withPreference
         *   _primary: 指查询只在主分片中查询
         *   _primary_first: 指查询会先在主分片中查询，如果主分片找不到（挂了），就会在副本中查询。
         *   _local: 指查询操作会优先在本地节点有的分片中查询，没有的话再在其它节点查询。
         *   _only_node:指在指定id的节点里面进行查询，如果该节点只有要查询索引的部分分片，就只在这部分分片中查找，所以查询结果可能不完整。
         *   Custom (string) value:用户自定义值，
         */
        // ********************************** query10 ********************************************
//        NativeSearchQuery query10 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withPreference("_only_nodes:USER-20200605UJ")
//                .build();

        /**
         * withIds      documentIds  这个在这里设置是没有作用的  必须配合 multiGet()
         * withFields  返回的字段
         */
        // ********************************** query11 ********************************************
//        NativeSearchQuery query11 = new NativeSearchQueryBuilder()
//                .withIds(Arrays.asList("1","2"))
//                .withFields("name", "sex","age","address")
//                .build();


        /**
         * withHighlightFields  高亮字段显示
         */
        // ********************************** query12 ********************************************
//        NativeSearchQuery query12 = new NativeSearchQueryBuilder()
//                .withQuery(wildcardQuery("name", "*小*"))
//                .withHighlightFields(new HighlightBuilder.Field("name"))
//                .build();


        /**
         * FieldSortBuilder  排序
         */
        // ********************************** query13 ********************************************
//        NativeSearchQuery query13 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withSort(new FieldSortBuilder("sex.keyword").order(SortOrder.ASC))
//                .withSort(new FieldSortBuilder("name.keyword").order(SortOrder.DESC))
//                .build();

        /**
         * FetchSourceFilterBuilder 返回字段过滤
         */
        // ********************************** query14 ********************************************
//        FetchSourceFilterBuilder sourceFilter = new FetchSourceFilterBuilder();
//        sourceFilter.withIncludes("name");
//        NativeSearchQuery query14 = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withSourceFilter(sourceFilter.build())
//                .build();

        /**
         * ik 分词
         */
        // ********************************** query15 ********************************************
        NativeSearchQuery query15 = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("北")
                        .field("address")
                        .analyzer("ik_max_word")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .build();


        SearchHits<Teacher> search = elasticsearchRestTemplate.search(query15, Teacher.class, teacher_index);
        search.getSearchHits().stream().map(t -> t.getContent().toString()).forEach(System.out::println);

        /**
         * 滚动查询
         */
        // ********************************** query16 ********************************************
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .withPageable(PageRequest.of(0, 2))
//                .build();
//        SearchScrollHits<Student> scroll =
//                elasticsearchRestTemplate.searchScrollStart(1000, searchQuery, Student.class, student_index);
//        String scrollId = scroll.getScrollId();
//        List<SearchHit<Student>> sampleEntities = new ArrayList<>();
//        while (scroll.hasSearchHits()) {
//            sampleEntities.addAll(scroll.getSearchHits());
//            scrollId = scroll.getScrollId();
//            scroll = elasticsearchRestTemplate.searchScrollContinue(scrollId, 1000, Student.class, student_index);
//        }
//        elasticsearchRestTemplate.searchScrollClear(Collections.singletonList(scrollId));
//
//        sampleEntities.stream().map(studentSearchHit -> studentSearchHit.getContent().toString()).forEach(System.out::println);

    }

}
