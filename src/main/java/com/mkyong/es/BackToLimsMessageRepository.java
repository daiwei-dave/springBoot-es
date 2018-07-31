package com.mkyong.es;

import com.alibaba.fastjson.JSON;


import com.mkyong.sdk.JacksonUtil;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 回传到limis返回错误信息记录
 * @author daiwei
 * @date 2018/5/19 16:14
 * @copyright(c) gome inc Gome Co.,LTD
 */
@Component
public class BackToLimsMessageRepository {
    private final static Logger logger = LoggerFactory.getLogger(BackToLimsMessageRepository.class);

    public EsBackToLimsMsg findById(String waybillNo) {
        GetResponse getResponse = ElasticsearchUtils.searchDataById(ElasticConstant.MESSAGE_BASK_LIMS_INDEX, ElasticConstant.MESSAGE_BASK_LIMS_TYPE, waybillNo, null);
        Map<String, Object> data=getResponse.getSource();

        EsBackToLimsMsg message = JacksonUtil.toObject(JacksonUtil.toJson(data), EsBackToLimsMsg.class);
        return message;
    }



    public void saveOrUpdate(EsBackToLimsMsg messege) {
        String waybillNo = messege.getWaybillNo();

        GetResponse getResponse = ElasticsearchUtils.searchDataById(ElasticConstant.MESSAGE_BASK_LIMS_INDEX, ElasticConstant.MESSAGE_BASK_LIMS_TYPE, waybillNo, null);
        Map<String, Object> data=getResponse.getSource();

        if (data == null) {
            String json = JSON.toJSONStringWithDateFormat(messege, "yyyy-MM-dd HH:mm:ss");
            ElasticsearchUtils.addData(json, ElasticConstant.MESSAGE_BASK_LIMS_INDEX, ElasticConstant.MESSAGE_BASK_LIMS_TYPE,waybillNo);
        } else {
            String json = JSON.toJSONStringWithDateFormat(messege, "yyyy-MM-dd HH:mm:ss");
            ElasticsearchUtils.updateDataById(json, ElasticConstant.MESSAGE_BASK_LIMS_INDEX, ElasticConstant.MESSAGE_BASK_LIMS_TYPE, waybillNo,getResponse.getVersion());
        }
    }


    public void save(EsBackToLimsMsg messege) {
        String waybillNo = messege.getWaybillNo();
        String json = JSON.toJSONStringWithDateFormat(messege, "yyyy-MM-dd HH:mm:ss");
        ElasticsearchUtils.addData(json, ElasticConstant.MESSAGE_BASK_LIMS_INDEX, ElasticConstant.MESSAGE_BASK_LIMS_TYPE, waybillNo);

    }

}
