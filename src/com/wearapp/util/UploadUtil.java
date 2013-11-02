package com.wearapp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class UploadUtil {

    private static String executeRequest(HttpRequestBase requestBase){

        String responseString = "" ;
        InputStream responseStream = null ;
        HttpClient client = new DefaultHttpClient () ;

        try{
            HttpResponse response = client.execute(requestBase) ;

            if (response != null){
                HttpEntity responseEntity = response.getEntity() ;

                if (responseEntity != null){
                    responseStream = responseEntity.getContent() ;

                    if (responseStream != null){
                        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream)) ;

                        String responseLine = br.readLine() ;
                        String tempResponseString = "" ;

                        while (responseLine != null){
                            tempResponseString = tempResponseString + responseLine + System.getProperty("line.separator") ;

                            responseLine = br.readLine() ;

                        }

                        br.close() ;

                        if (tempResponseString.length() > 0){
                            responseString = tempResponseString ;

                        }
                    }
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }finally{

            if (responseStream != null){
                try {
                    responseStream.close() ;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        client.getConnectionManager().shutdown() ;

        return responseString ;
    }

    public String executeMultiPartRequest(String urlString, File file, String fileName, String fileDescription) {

        HttpPost postRequest = new HttpPost (urlString) ;
        try{

            MultipartEntity multiPartEntity = new MultipartEntity () ;

            //The usual form parameters can be added this way

            multiPartEntity.addPart("fileDescription", new StringBody(fileDescription != null ? fileDescription : "")) ;

            multiPartEntity.addPart("fileName", new StringBody(fileName != null ? fileName : file.getName())) ;

            FileBody fileBody = new FileBody(file, "application/octect-stream") ;
            multiPartEntity.addPart("attachment", fileBody) ;
            postRequest.setEntity(multiPartEntity) ;

        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace() ;

        }

        return executeRequest (postRequest) ;

    }
}
