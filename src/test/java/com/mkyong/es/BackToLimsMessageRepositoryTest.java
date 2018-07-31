package com.mkyong.es;

import com.mkyong.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author daiwei
 * @date 2018/7/31 11:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BackToLimsMessageRepositoryTest {

    @Autowired
    private BackToLimsMessageRepository backToLimsMessageRepository;
    @Test
    public void findById() {
    }

    @Test
    public void saveOrUpdate() {
    }


    @Test
    public void testHelloWorld() {
        System.out.println("ahah");
    }


    @Test
    public void save() {
        EsBackToLimsMsg esBackToLimsMsg=new EsBackToLimsMsg();
        esBackToLimsMsg.setWaybillNo("1111");
        esBackToLimsMsg.setInterfaceCode("2222");
        esBackToLimsMsg.setReqInfoXML("3333");
        esBackToLimsMsg.setBusinessXml("4444");
        esBackToLimsMsg.setRespCode("5555");
        esBackToLimsMsg.setRespMsg("6666");
        backToLimsMessageRepository.save(esBackToLimsMsg);
    }
}