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

    @Autowired
    private EsBackToLimsMsgRepository esBackToLimsMsgRepository;


    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Test
    public void testSave(){
        EsBackToLimsMsg esBackToLimsMsg=new EsBackToLimsMsg();
        esBackToLimsMsg.setWaybillNo("1111");
        esBackToLimsMsg.setInterfaceCode("2222");
        esBackToLimsMsg.setReqInfoXML("3333");
        esBackToLimsMsg.setBusinessXml("4444");
        esBackToLimsMsg.setRespCode("5555");
        esBackToLimsMsg.setRespMsg("6666");
        esBackToLimsMsgRepository.save(esBackToLimsMsg);
    }

    @Test
    public void testCreateIndex(){
        esTemplate.createIndex(EsBackToLimsMsg.class);
    }

}