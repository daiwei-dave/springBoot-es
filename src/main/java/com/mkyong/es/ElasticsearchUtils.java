package com.mkyong.es;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhangliewei
 * @date 2018/5/11 11:38
 *
 */
@Component
public class ElasticsearchUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchUtils.class);

    @Autowired
    private TransportClient transportClient;

    private static TransportClient client;

    @PostConstruct
    public void init() {
        client = this.transportClient;
    }

    /**
     * 创建索引
     * @param index
     * @return
     */
    public static boolean createIndex(String index) {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }
        CreateIndexResponse indexresponse = client.admin().indices().prepareCreate(index).execute().actionGet();
        LOGGER.info("执行建立成功？" + indexresponse.isAcknowledged());

        return indexresponse.isAcknowledged();
    }

    /**
     * 删除索引
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index) {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }
        DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
        if (dResponse.isAcknowledged()) {
            LOGGER.info("delete index " + index + "  successfully!");
        } else {
            LOGGER.info("Fail to delete index " + index);
        }
        return dResponse.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     * @param index
     * @return
     */
    public static boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        if (inExistsResponse.isExists()) {
            LOGGER.info("Index [" + index + "] is exist!");
        } else {
            LOGGER.info("Index [" + index + "] is not exist!");
        }
        return inExistsResponse.isExists();
    }

    /**
     * 数据添加，正定ID
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type, String id) {
        IndexResponse response = client.prepareIndex(index, type, id).setSource(jsonObject).get();
        LOGGER.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }

    /**
     * 数据添加，正定ID
     * @param json  格式数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     * @return
     */
    public static String addData(String json, String index, String type, String id) {
        IndexRequestBuilder requestBuilder = client.prepareIndex(index, type, id);
        IndexResponse response = requestBuilder.setSource(json, XContentType.JSON).get();
        LOGGER.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }

    /**
     * 数据添加
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type) {
        return addData(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过ID删除数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public static void deleteDataById(String index, String type, String id) {
        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        LOGGER.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
    }

    /**
     * 通过ID 更新数据
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static void updateDataById(JSONObject jsonObject, String index, String type, String id) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).type(type).id(id).doc(jsonObject);
        client.update(updateRequest);
    }

    /**
     * 通过ID 更新数据
     * @param json  要增加的数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     * @return
     */
    public static void updateDataById(String json, String index, String type, String id, Long version) {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            if (version != null) {
                updateRequest.version(version);
            }
            updateRequest.index(index).type(type).id(id).doc(json, XContentType.JSON);
            ActionFuture<UpdateResponse> responseActionFuture = client.update(updateRequest);
            UpdateResponse updateResponse = responseActionFuture.get();
            if (updateResponse.status().getStatus() != 200) {
                throw new RuntimeException(String.format("更新ES失败[index:%s,type:%s,id:%s,json:%s]", index, type, id, json, json));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("更新ES失败[index:%s,type:%s,id:%s,json:%s]", index, type, id, json, json), e);
        }
    }

    /**
     * 通过ID获取数据
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static GetResponse searchDataById(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
//        Map<String, Object> map = getResponse.getSource();
        return getResponse;
    }

    /**
     * 使用分词查询
     * @param index    索引名称
     * @param type     类型名称,可传入多个type逗号分隔
     * @param fields   需要显示的字段，逗号分隔（缺省为全部字段）
     * @param matchStr 过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, String fields, String matchStr) {
        return searchListData(index, type, 0, 0, null, fields, null, false, null, matchStr);
    }

    /**
     * 使用分词查询
     * @param index       索引名称
     * @param type        类型名称,可传入多个type逗号分隔
     * @param fields      需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField   排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param matchStr    过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, String fields, String sortField, boolean matchPhrase, String matchStr) {
        return searchListData(index, type, 0, 0, null, fields, sortField, matchPhrase, null, matchStr);
    }

    /**
     * 使用分词查询
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, highlightField, matchStr);
    }

    /**
     * 使用分词查询
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (startTime > 0 && endTime > 0) {
            boolQuery.must(QueryBuilders.rangeQuery("processTime")
                    .format("epoch_millis")
                    .from(startTime)
                    .to(endTime)
                    .includeLower(true)
                    .includeUpper(true));
        }

        //搜索的的字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(",")) {
                String[] ss = s.split("=");
                if (ss.length > 1) {
                    if (matchPhrase == Boolean.TRUE) {
                        boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]));
                    } else {
                        boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
                    }
                }

            }
        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }

        searchRequestBuilder.setQuery(boolQuery);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        LOGGER.info("\n{}", searchRequestBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits;
        long length = searchResponse.getHits().getHits().length;

        LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }

        return null;

    }

    public static List<Map<String, Object>> searchListData(String index, String type, List<String> ids) {
        List<Map<String, Object>> list = new ArrayList<>();
        MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();
        multiGetRequestBuilder.add(index, type, ids);
        MultiGetResponse multiGetResponse = multiGetRequestBuilder.execute().actionGet();
        for (MultiGetItemResponse itemResponse : multiGetResponse) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                Map source = response.getSource();
                list.add(source);
            }
        }
        return list;
    }

    public static List<Map<String, Object>> searchListData(String index, String type, int start, int size, QueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
        searchRequestBuilder.setFrom(start).setSize(size);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        if (searchResponse.status().getStatus() == 200) {
            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, null);
            return sourceList;
        }
        return null;
    }

    /**
     * 使用分词查询,并分页
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param currentPage    当前页
     * @param pageSize       每页显示条数
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @return
     */
    public static EsPage searchDataPage(String index, String type, int currentPage, int pageSize, long startTime, long endTime, String fields, String sortField, boolean matchPhrase, String highlightField, QueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);


        // 需要显示的字段，逗号分隔（缺省为全部字段）
        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }

        //排序字段
        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

//        if (startTime > 0 && endTime > 0) {
//            boolQuery.must(QueryBuilders.rangeQuery("processTime")
//                    .format("epoch_millis")
//                    .from(startTime)
//                    .to(endTime)
//                    .includeLower(true)
//                    .includeUpper(true));
//        }

        // 查询字段
//        if (StringUtils.isNotEmpty(matchStr)) {
//            for (String s : matchStr.split(",")) {
//                String[] ss = s.split("=");
//                if (matchPhrase == Boolean.TRUE) {
//                    boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]));
//                } else {
//                    boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
//                }
//            }
//        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }

//        searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        searchRequestBuilder.setQuery(queryBuilder);

        // 分页应用
        searchRequestBuilder.setFrom((currentPage - 1) * pageSize).setSize(pageSize);

        // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        LOGGER.info("\n{}", searchRequestBuilder);

        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse = null;
        try {
            searchResponse = searchRequestBuilder.execute().actionGet();
        } catch (Exception e) {
            return  new EsPage(currentPage, pageSize, 0, new ArrayList<>());
        }

        long totalHits = searchResponse.getHits().totalHits;
        long length = searchResponse.getHits().getHits().length;

        LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);

            return new EsPage(currentPage, pageSize, (int) totalHits, sourceList);
        }

        return null;

    }

    public static Long count(String index, String type, QueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
        searchRequestBuilder.setQuery(queryBuilder);
        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        long totalHits = searchResponse.getHits().totalHits;
        return totalHits;
    }

    /**
     * 高亮结果集 特殊处理
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
            if (StringUtils.isNotEmpty(highlightField)) {
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();
                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }

//    /**
//     * 获取scrollId
//     * @param index
//     * @param type
//     * @param size
//     * @return
//     */
//    public static EsListObject searchByScroll(String index, String type, int size) {
//        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
//        searchRequestBuilder.setTypes(type);
//        searchRequestBuilder.setSize(size);
//        searchRequestBuilder.setScroll(new TimeValue(30000));
//        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
//        SearchResponse searchResponse = searchRequestBuilder.get();
//        String scrollId = searchResponse.getScrollId();
//        if (searchResponse.getHits().getHits().length == 0) {
//            return null;
//        }
//
//        List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, null);
//        EsListObject esListObject = new EsListObject(scrollId, sourceList);
//        return esListObject;
//    }
//
//    /**
//     * 根据 scrollId 获取数据
//     * @param scrollId
//     */
//    public static EsListObject searchByScrollId(String scrollId) {
//        TimeValue timeValue = new TimeValue(30000);
//        SearchScrollRequestBuilder searchScrollRequestBuilder = client.prepareSearchScroll(scrollId);
//        searchScrollRequestBuilder.setScroll(timeValue);
//        SearchResponse searchResponse = searchScrollRequestBuilder.get();
//        if (searchResponse.getHits().getHits().length == 0) {
//            return null;
//        }
//        List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, null);
//        EsListObject esListObject = new EsListObject(searchResponse.getScrollId(), sourceList);
//        return esListObject;
//    }
}
