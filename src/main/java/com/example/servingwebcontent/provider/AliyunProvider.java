package com.example.servingwebcontent.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
public class AliyunProvider {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    public String upload(InputStream fileStream,String fileName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String generateFileName = "";
        String[] filePaths = fileName.split("\\.");
        if(filePaths.length>1){
            generateFileName = UUID.randomUUID().toString()+"."+filePaths[filePaths.length-1];
        }else {
            return null;
        }
        ossClient.putObject("community-czt", generateFileName, fileStream);
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl("community-czt",generateFileName, expiration);
        System.out.println("url"+url);
        ossClient.shutdown();
        if (url != null) {
            return url.toString();
        }
        return null;
    }
    
}
