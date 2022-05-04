package br.senai.sp.cfp138.restaguide.util;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

public class HashUtil {
	public static String hash256(String palavra) {
		// "tempero" para o hash
		String salt = "b@n@n@";
		// acrescento o tempero
		palavra = palavra + salt;
		// criar o hash e armazenar na String
		String sha256 = 
				Hashing.sha256().hashString(palavra, StandardCharsets.UTF_8).toString();
		// retorna o hash
		return sha256;
	}
}
