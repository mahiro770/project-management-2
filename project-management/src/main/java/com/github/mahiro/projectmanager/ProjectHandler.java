package com.github.mahiro.projectmanager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// /api/projects と /api/projects/{id} をまとめて処理するハンドラ
public class ProjectHandler implements HttpHandler {

    private static final Pattern ITEM_PATH = Pattern.compile("^/api/projects/(\\d+)/?$");
    private static final Pattern APPLIED_PATH = Pattern.compile("^/api/projects/(\\d+)/applied/?$");
    private static final Pattern CATEGORY_PATH = Pattern.compile("^/api/projects/(\\d+)/category/?$");

    private final ProjectService service = new ProjectService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            addCorsHeaders(exchange);

            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            Matcher itemMatcher = ITEM_PATH.matcher(path);
            Matcher appliedMatcher = APPLIED_PATH.matcher(path);
            Matcher categoryMatcher = CATEGORY_PATH.matcher(path);

            if ("/api/projects".equals(path) || "/api/projects/".equals(path)) {
                switch (method) {
                    case "GET" -> handleList(exchange);
                    case "POST" -> handleCreate(exchange);
                    default -> sendError(exchange, 405, "METHOD_NOT_ALLOWED", "許可されていないメソッドです。");
                }
                return;
            }

            if (appliedMatcher.matches()) {
                int id = Integer.parseInt(appliedMatcher.group(1));
                if ("PUT".equals(method)) {
                    handleSetApplied(exchange, id);
                } else {
                    sendError(exchange, 405, "METHOD_NOT_ALLOWED", "許可されていないメソッドです。");
                }
                return;
            }

            if (categoryMatcher.matches()) {
                int id = Integer.parseInt(categoryMatcher.group(1));
                if ("PUT".equals(method)) {
                    handleSetCategory(exchange, id);
                } else {
                    sendError(exchange, 405, "METHOD_NOT_ALLOWED", "許可されていないメソッドです。");
                }
                return;
            }

            if (itemMatcher.matches()) {
                int id = Integer.parseInt(itemMatcher.group(1));
                switch (method) {
                    case "GET" -> handleDetail(exchange, id);
                    case "PUT" -> handleUpdate(exchange, id);
                    case "DELETE" -> handleDelete(exchange, id);
                    default -> sendError(exchange, 405, "METHOD_NOT_ALLOWED", "許可されていないメソッドです。");
                }
                return;
            }

            sendError(exchange, 404, "NOT_FOUND", "指定されたパスが見つかりません。");

        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, "VALIDATION_ERROR", e.getMessage());
        } catch (ProjectNotFoundException e) {
            sendError(exchange, 404, "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "INTERNAL_ERROR", "サーバー内部でエラーが発生しました。");
        }
    }

    private void handleList(HttpExchange exchange) throws IOException {
        JSONArray body = ProjectJson.toJsonArray(service.getProjectList());
        sendJson(exchange, 200, body);
    }

    private void handleDetail(HttpExchange exchange, int id) throws IOException {
        Project project = service.getProjectDetail(id);
        sendJson(exchange, 200, ProjectJson.toJson(project));
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        JSONObject requestJson = readJsonBody(exchange);

        Project created = service.registerProject(
                ProjectJson.optString(requestJson, "title"),
                ProjectJson.optString(requestJson, "clientName"),
                ProjectJson.optString(requestJson, "requiredSkills"),
                ProjectJson.optString(requestJson, "location"),
                ProjectJson.optString(requestJson, "priceMin"),
                ProjectJson.optString(requestJson, "priceMax"),
                ProjectJson.optString(requestJson, "category"));

        sendJson(exchange, 201, ProjectJson.toJson(created));
    }

    private void handleUpdate(HttpExchange exchange, int id) throws IOException {
        JSONObject requestJson = readJsonBody(exchange);

        Project updated = service.updateProject(
                id,
                ProjectJson.optString(requestJson, "title"),
                ProjectJson.optString(requestJson, "clientName"),
                ProjectJson.optString(requestJson, "requiredSkills"),
                ProjectJson.optString(requestJson, "location"),
                ProjectJson.optString(requestJson, "priceMin"),
                ProjectJson.optString(requestJson, "priceMax"),
                ProjectJson.optString(requestJson, "status"));

        sendJson(exchange, 200, ProjectJson.toJson(updated));
    }

    private void handleSetApplied(HttpExchange exchange, int id) throws IOException {
        JSONObject requestJson = readJsonBody(exchange);
        boolean applied = requestJson.optBoolean("applied", false);

        Project updated = service.setApplied(id, applied);

        sendJson(exchange, 200, ProjectJson.toJson(updated));
    }

    private void handleSetCategory(HttpExchange exchange, int id) throws IOException {
        JSONObject requestJson = readJsonBody(exchange);
        String category = ProjectJson.optString(requestJson, "category");

        Project updated = service.setCategory(id, category);

        sendJson(exchange, 200, ProjectJson.toJson(updated));
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        service.deleteProject(id);
        exchange.sendResponseHeaders(204, -1);
    }

    private JSONObject readJsonBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                return new JSONObject();
            }
            return new JSONObject(body);
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int status, String code, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", new JSONObject().put("code", code).put("message", message));
        sendJson(exchange, status, error);
    }
}
