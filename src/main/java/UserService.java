import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class UserService {

    private final String BASE_URL = "https://jsonplaceholder.typicode.com/users";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public User createUser(User user) throws IOException, InterruptedException {
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), User.class);
    }

    public User updateUser(User user) throws IOException, InterruptedException {
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + user.getId()))
                .header("Content-Type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (body == null || body.isBlank() || !body.trim().startsWith("{")) {
            return user;
        }
        return gson.fromJson(body, User.class);
    }

    public boolean deleteUser(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() / 100 == 2;
    }

    public List<User> getAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User[] users = gson.fromJson(response.body(), User[].class);
        return Arrays.asList(users);
    }

    public User getUserById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), User.class);
    }

    public List<User> getUserByUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?username=" + username))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User[] users = gson.fromJson(response.body(), User[].class);
        return Arrays.asList(users);
    }

    static class Post {
        private int id;
        private int userId;
        private String title;
        private String body;

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }

    static class Comment {
        private int id;
        private int postId;
        private String name;
        private String email;
        private String body;

        public int getId() {
            return id;
        }

        public int getPostId() {
            return postId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getBody() {
            return body;
        }
    }

    static class Todo {
        private int id;
        private int userId;
        private String title;
        private boolean completed;

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getTitle() {
            return title;
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    public void saveCommentsOfLastPost(int userId) throws IOException, InterruptedException {
        HttpRequest postsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/posts"))
                .GET()
                .build();
        HttpResponse<String> postsResponse = client.send(postsRequest, HttpResponse.BodyHandlers.ofString());
        Post[] posts = gson.fromJson(postsResponse.body(), Post[].class);

        if (posts.length == 0) {
            System.out.println("У пользователя нет постов.");
            return;
        }

        Post lastPost = posts[0];
        for (Post p : posts) {
            if (p.getId() > lastPost.getId()) lastPost = p;
        }
        int postId = lastPost.getId();

        HttpRequest commentsRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/" + postId + "/comments"))
                .GET()
                .build();
        HttpResponse<String> commentsResponse = client.send(commentsRequest, HttpResponse.BodyHandlers.ofString());
        Comment[] comments = gson.fromJson(commentsResponse.body(), Comment[].class);

        String fileName = "user-" + userId + "-post-" + postId + "-comments.json";
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
            gson.toJson(comments, writer);
        }

        System.out.println("Комментарии сохранены в файл: " + fileName);
    }

    public void printOpenTodos(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/todos"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Todo[] todos = gson.fromJson(response.body(), Todo[].class);

        System.out.println("Открытые задачи пользователя " + userId + ":");
        for (Todo todo : todos) {
            if (!todo.isCompleted()) {
                System.out.println("ID: " + todo.getId() + " | " + todo.getTitle());
            }
        }
    }
}