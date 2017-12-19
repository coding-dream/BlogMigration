package com.less.migration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.less.migration.BlogHttpClient.Callback;

public class BlogProcessor {

	private static final String regexStr = "!\\[.*\\]\\(([a-zA-z]+://[^\\s]*)\\)";
	private String rootFolder;
	private String destImageFolder;

	private Pattern regex;

	private int count = 0;

	static public BlogProcessor getDefault() {
        return gDefault.get();
    }

    private static final Singleton<BlogProcessor> gDefault = new Singleton<BlogProcessor>() {

		@Override
		protected BlogProcessor create() {
			BlogProcessor blogProcessor = new BlogProcessor();
			blogProcessor.init();
			return blogProcessor;
		}
    };


    private void init() {
    	regex = Pattern.compile(regexStr);
	}

    public BlogProcessor rootFolder(String rootFolder){
    	this.rootFolder = rootFolder;
    	return this;
    }

    public BlogProcessor destImageFolder(String destImageFolder){
    	this.destImageFolder = destImageFolder;
    	return this;
    }

	public void process() {
		File rootFile = new File(rootFolder);
		List<File> folders = listFolders(rootFile);

		startWork(folders);
		System.out.println("========= ����" + count + "ƪ������������� =========");
	}

	/**
	 * ��������md�ļ�
	 * @param folders
	 */
	private void startWork(List<File> folders) {
		for(File folder : folders){
			for(File article : folder.listFiles()){
				if(article.isFile()){
					count ++;
					sendRequest(article);
				}
			}
		}
	}

	/**
	 * ��������
	 * @param article
	 */
	private void sendRequest(File article) {
		try {
			// ��Դӳ��
			Map<String,String> map = new HashMap<>();
			map.put("name", article.getName());
			map.put("category", article.getParentFile().getName());
			map.put("path", article.getAbsolutePath());

			String md = FileUtils.readFileToString(article, "UTF-8");
			Matcher matcher = regex.matcher(md);
			while (matcher.find()) {
				String url = matcher.group(1);
				map.put("srcImage", url);
				BlogHttpClient.getDefault().sendRequest(url, map, new Callback() {

					@Override
					public void done(byte[] ret, Map<String, String> params, Exception e) {
						if(null == e){
							updateArticle(ret,params);
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ͼƬ�ļ�������md�ļ�ͼƬ����ӳ��
	 * @param ret
	 * @param params
	 */
	protected synchronized void updateArticle(byte[] ret, Map<String, String> params) {
		try {
			String name = params.get("name");
			String category = params.get("category");
			String srcImage = params.get("srcImage");
			String path = params.get("path");
			String md = FileUtils.readFileToString(new File(path), "UTF-8");

			if(null != srcImage){
				String md5 = DigestUtils.md5Hex(srcImage);
				// д��ͼƬ
				File destImage = new File(destImageFolder + File.separator + md5 + ".png");
				if(!destImage.getParentFile().exists()){
					destImage.getParentFile().mkdirs();
				}
				FileUtils.writeByteArrayToFile(destImage, ret);
				// ������������
				String mdUpdate = md.replace(srcImage, "../images/" + md5 + ".png");
				// д��md�ļ�,ֱ����Դ�ļ���д,��Ϊ���ļ�������ڶ���ͼƬ�ᱻ��ζ����д��,���û��ͼƬ���޲���.
				File destArticle = new File(path);
				if(!destArticle.getParentFile().exists()){
					destArticle.getParentFile().mkdirs();
				}
				FileUtils.writeStringToFile(destArticle, mdUpdate, "UTF-8");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * �г���Ŀ����е����Ŀ¼
	 * @param rootFile md�ļ���Ŀ�
	 * @return
	 */
	private List<File> listFolders(File rootFile) {
		List<File> list = new ArrayList<File>();
		for(File folder : rootFile.listFiles()){
			if(folder.isDirectory()){
				list.add(folder);
			}
		}
		list.add(rootFile);
		return list;
	}
}
