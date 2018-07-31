package com.mkyong;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.net.InetAddress;


@Configuration
public class EsConfig {

    @Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;

    @Bean
    public TransportClient transportClient() throws Exception {
        TransportClient transportClient = null;
        // 设置配置信息
        Settings esSettings = Settings.builder()
                .put("cluster.name", EsClusterName)
                .build();

        transportClient = new PreBuiltTransportClient(esSettings);

        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(EsHost), EsPort);
        transportClient.addTransportAddress(transportAddress);
        return transportClient;
    }





}