package com.less.migration;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private final static String TAG = HttpClient.class.getSimpleName();
    private CountableThreadPool threadPool = new CountableThreadPool(5);
    private static final int TRY_TIMES = 3;

    static public HttpClient getDefault() {
        return gDefault.get();
    }

    private static final Singleton<HttpClient> gDefault = new Singleton<HttpClient>() {

		@Override
		protected HttpClient create() {
			HttpClient httpGet = new HttpClient();
			return httpGet;
		}
    };

    public interface Callback {

        Callback NONE = new Callback() {
            @Override
            public <T> void done(T ret, Exception e) {
                if(null != e)
                    throw new RuntimeException(e);
            }
        };

        <T> void done(T ret, Exception e);
    }


    public void sendRequest(final String url, final Callback callback) {
        sendRequest(url, null, callback);
    }

    private void sendRequest(final String url, final Map<String, String> params, final Callback callback){

        Map<String, String> req = new HashMap<>();
        if(null != params){
            req.putAll(params);
        }

        // req.put("xx", xx);

        this.call(url, req, callback);
    }

    private void call(final String api, final Map<String, String> map, final Callback callback) {
		threadPool.execute(new CallTask(api, map, callback, TRY_TIMES));
    }


    private byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] datas = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return datas;
    }

    class CallTask implements Runnable {

        final String api;
        final Map<String, String> map;
        final Callback callback;

        CallTask(final String api, final Map map, final Callback wrapcall, final int trytimes){
            this.api = api;
            this.map = map;

            callback = new Callback() {
                @Override
                public <T> void done(T ret, Exception e) {
                    if(trytimes > 1 && null != e) {
                    	System.out.println("call api - " + api + " failed: " + e);
                        threadPool.execute(new CallTask(api, map, wrapcall, trytimes - 1));
                    } else {
                        wrapcall.done(ret, e);
                    }
                }
            };

            System.out.println("call api - " + api + " " + trytimes + " times");
        }

        @Override
        public void run() {

            StringBuffer params = new StringBuffer();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
                connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                byte[] datas = readInputStream(connection.getInputStream());
                callback.done(datas,null);

            } catch (Exception e) {
                callback.done(null, e);
            }
        }
    }
}
