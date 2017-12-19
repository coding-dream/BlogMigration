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
		System.out.println("========= 您的" + count + "篇文章已下载完毕 =========");
	}

	/**
	 * 遍历所有md文件
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
	 * 发送请求
	 * @param article
	 */
	private void sendRequest(File article) {
		try {
			// 资源映射
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
	 * 保存图片文件并更新md文件图片链接映射
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
				// 写入图片
				File destImage = new File(destImageFolder + File.separator + md5 + ".png");
				if(!destImage.getParentFile().exists()){
					destImage.getParentFile().mkdirs();
				}
				FileUtils.writeByteArrayToFile(destImage, ret);
				// 更新文章内容
				String mdUpdate = md.replace(srcImage, "../images/" + md5 + ".png");
				// 写入md文件,直接在源文件中写,因为该文件如果存在多张图片会被多次读入和写入,如果没有图片则无操作.
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
	 * 列出根目錄所有的类别目录
	 * @param rootFile md文件根目錄
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
