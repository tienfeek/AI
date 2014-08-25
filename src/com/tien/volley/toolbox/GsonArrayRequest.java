package com.tien.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import org.json.JSONArray;
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
public class GsonArrayRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Type type;
    private final Map<String, String> headers;
    private final Listener<T> listener;
 
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonArrayRequest(int method, String url, Type type, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
    }
    
    public GsonArrayRequest( String url, Map<String, String> params, Type type, Listener<T> listener,
            ErrorListener errorListener) {
        super(Method.POST, url, errorListener, params);
        this.type = type;
        this.headers = null;
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
 
    @SuppressWarnings("unchecked")
	@Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            XLog.i("wanges", "GsonArrayRequest:" +json);
            JSONObject object = new JSONObject(json);
            JSONArray arry = object.getJSONArray("data");
            T obj =  (T)gson.fromJson(arry.toString(), type);
            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }catch (Exception e) {
        	return Response.error(new ParseError(e));
        }
    }
}