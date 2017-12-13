package com.less.migration;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BlogHttpClient {

    private final static String TAG = HttpClient.class.getSimpleName();
    private CountableThreadPool threadPool = new CountableThreadPool(5);
    private static final int TRY_TIMES = 3;

    static public BlogHttpClient getDefault() {
        return gDefault.get();
    }

    private static final Singleton<BlogHttpClient> gDefault = new Singleton<BlogHttpClient>() {

		@Override
		protected BlogHttpClient create() {
			BlogHttpClient blogHttpClient = new BlogHttpClient();
			return blogHttpClient;
		}
    };

    public interface Callback {

        Callback NONE = new Callback() {
            @Override
            public void done(byte[] datas, Map<String,String> params, Exception e) {
                if(null != e)
                    throw new RuntimeException(e);
            }
        };

        void done(byte[] ret, Map<String,String> params, Exception e);
    }


    public void sendRequest(final String url, Map<String, String> params,Callback callback){

        Map<String, String> req = new HashMap<>();
        if(null != params){
            req.putAll(params);
        }

        this.call(url, req, callback);
    }

    private void call(final String api, final Map<String, String> req, final Callback callback) {
		threadPool.execute(new CallTask(api, req, callback, TRY_TIMES));
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
        final Map<String, String> mParams;
        final Callback callback;

        CallTask(final String api, final Map map, final Callback wrapcall, final int trytimes){
            this.api = api;
            this.mParams = map;
            callback = new Callback() {

				@Override
				public void done(byte[] datas, Map<String, String> params, Exception e) {
					if(trytimes > 1 && null != e) {
                    	System.out.println("call api - " + api + " failed: " + e);
                        threadPool.execute(new CallTask(api, params, wrapcall, trytimes - 1));
                    } else {
                    	// success
                        wrapcall.done(datas,params,e);
                    }
				}
            };

            System.out.println("call api - " + api + " " + trytimes + " times");
        }

        @Override
        public void run() {

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
                connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                byte[] datas = readInputStream(connection.getInputStream());
                // 首先回调给自己callback处理,然后再回调给用户wrapcall处理.
                callback.done(datas,mParams,null);

            } catch (Exception e) {
                callback.done(null,null, e);
            }
        }
    }
}
