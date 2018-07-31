package com.mkyong.book.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "message-back-lims-index", type = "message-back-lims-type")
public class EsBackToLimsMsg {

    @Id
    private String waybillNo;
    private String respCode;
    private String respMsg;


    private String interfaceCode;
    private String reqInfoXML;
    private String businessXml;


    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getReqInfoXML() {
        return reqInfoXML;
    }

    public void setReqInfoXML(String reqInfoXML) {
        this.reqInfoXML = reqInfoXML;
    }

    public String getBusinessXml() {
        return businessXml;
    }

    public void setBusinessXml(String businessXml) {
        this.businessXml = businessXml;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
