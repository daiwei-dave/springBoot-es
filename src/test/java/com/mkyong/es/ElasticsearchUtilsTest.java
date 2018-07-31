package com.mkyong.es;

import com.mkyong.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author daiwei
 * @date 2018/7/31 11:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ElasticsearchUtilsTest {



    @Test
    public void init() {
    }

    @Test
    public void createIndex() {
        ElasticsearchUtils.createIndex(ElasticConstant.MESSAGE_BASK_LIMS_INDEX);
    }

    @Test
    public void deleteIndex() {
    }

    @Test
    public void isIndexExist() {
    }

    @Test
    public void addData() {
    }

    @Test
    public void addData1() {
    }

    @Test
    public void addData2() {
    }

    @Test
    public void deleteDataById() {
    }

    @Test
    public void updateDataById() {
    }
}