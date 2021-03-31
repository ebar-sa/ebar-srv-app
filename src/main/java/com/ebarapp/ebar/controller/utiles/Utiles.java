package com.ebarapp.ebar.controller.utiles;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.buf.StringUtils;


public class Utiles {
	private static final String bancoCaracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	
	public static String getRandomToken(int length, int cycles) {
		List<String> claves = new ArrayList<String>();
		for (int c = 0; c<cycles; c++) {
			StringBuilder token = new StringBuilder();
			for(int i = 0; i<length; i++) {
				token.append(bancoCaracteres.charAt(new SecureRandom().nextInt(bancoCaracteres.length())));
			}
			claves.add(token.toString());
		}
		return StringUtils.join(claves);
	}
}
