/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.vaqxine.NetworkManager.struct;

import java.net.Socket;
import lombok.Getter;

/**
 * 
 * @author devan_000
 */
public class QueryRequest {

	@Getter
	private final Socket socket;
	@Getter
	private final String[] query;

	public QueryRequest(Socket socket, String[] query) {
		this.socket = socket;
		this.query = query;
	}

}
