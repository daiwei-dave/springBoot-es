package com.mkyong.book.repository;

import com.mkyong.Application;
import com.mkyong.book.model.EsBackToLimsMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author daiwei
 * @date 2018/7/31 13:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class EsBackToLimsMsgRepositoryTest {

    private BookRepository bookRepository;


    @Autowired
    private ElasticsearchTemplate esTemplate;


    public void testSave(){

    }

    @Test
    public void testCreateIndex(){
        esTemplate.createIndex(EsBackToLimsMsg.class);
    }

}