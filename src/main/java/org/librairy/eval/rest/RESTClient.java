package org.librairy.eval.rest;

import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class RESTClient {

    private static final Logger LOG = LoggerFactory.getLogger(RESTClient.class);

    private final String host;

    public RESTClient(String host){
        this.host = host;
    }

    public JSONObject getResource(String uri) throws UnsupportedEncodingException, UnirestException {
        String url = host + URLEncoder.encode(uri,"UTF-8");

        HttpResponse<JsonNode> response = Unirest.get(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asJson();

        if (response.getStatus() != 200){
            throw new UnirestException("Http error: " + response.getStatus());
        }

        return response.getBody().getObject();
    }

    public JSONArray getList(String uri) throws UnsupportedEncodingException, UnirestException {
        String url = host + URLEncoder.encode(uri,"UTF-8");

        HttpResponse<JsonNode> response = Unirest.get(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asJson();

        if (response.getStatus() != 200){
            throw new UnirestException("Http error: " + response.getStatus());
        }

        return response.getBody().getArray();
    }


}
