package com.github.mahiro.projectmanager;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

// フロントエンド(JS)から呼び出すためのREST APIサーバー起動エントリーポイント
public class ApiMain {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Database.migrate();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/projects", new ProjectHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("APIサーバーを起動しました: http://localhost:" + PORT + "/api/projects");
    }
}
