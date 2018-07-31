package com.mkyong.book.repository;


import com.mkyong.book.model.EsBackToLimsMsg;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsBackToLimsMsgRepository extends ElasticsearchRepository<EsBackToLimsMsg, String> {


}