package com.colin.java.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestExpress {

	public static void main(String[] args) {
		log.info("{}", false || true || true && false);
	}

}