package com.tien.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tien.ai.utils.XLog;
import com.tien.volley.AuthFailureError;
import com.tien.volley.NetworkResponse;
import com.tien.volley.ParseError;
import com.tien.volley.Request;
import com.tien.volley.Response;
import com.tien.volley.Response.ErrorListener;
import com.tien.volley.Response.Listener;


/**
 * @Description:Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-2-27
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Listener<T> listener;
 
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }
    
    public GsonRequest(String url, Class<T> clazz, Map<String, String> params,
            Listener<T> listener, ErrorListener errorListener) {
    	this(url, clazz, params, null,listener, errorListener);
    }
    
    public GsonRequest(String url, Class<T> clazz, Map<String, String> params, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(Method.POST, url, errorListener, params);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }
 
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
 
    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
 
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            XLog.i("wanges", "GsonRequest:"+json);
            
            JSONObject jsonObject = new JSONObject(json);
            if(clazz == String.class){
            	String data = jsonObject.getString("data");
            	return Response.success(gson.fromJson(data, clazz), HttpHeaderParser.parseCacheHeaders(response));
            }
        	JSONObject data = jsonObject.getJSONObject("data");
        	return Response.success(gson.fromJson(data.toString(), clazz), HttpHeaderParser.parseCacheHeaders(response));
            
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
        	 return Response.error(new ParseError(e));
		}
    }
}