package com.less.migration;

public class App {

	public static void main(String[] args) {
		BlogProcessor.getDefault()
			// 博客解压后的根目录
			.rootFolder("F:\\JianShu\\blogs")
			// 图片的保存目录
			.destImageFolder("F:\\JianShu\\images")
			.process();
	}
}
