package com.less.migration;

public class App {

	public static void main(String[] args) {
		BlogProcessor.getDefault()
			// ���ͽ�ѹ��ĸ�Ŀ¼
			.rootFolder("F:\\JianShu\\blogs")
			// ͼƬ�ı���Ŀ¼
			.destImageFolder("F:\\JianShu\\images")
			.process();
	}
}
