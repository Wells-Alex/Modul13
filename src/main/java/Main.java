import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        UserService service = new UserService();

        User newUser = new User("Alex", "alex123", "alex@example.com");
        User created = service.createUser(newUser);
        System.out.println("Создан пользователь ID: " + created.getId());

        List<User> users = service.getAllUsers();
        System.out.println("Всего пользователей: " + users.size());

        User user1 = service.getUserById(1);
        System.out.println("ID 1: " + user1.getName());

        List<User> byUsername = service.getUserByUsername("Bret");
        System.out.println("По username Bret: " + byUsername.getFirst().getName());

        created.setEmail("new@example.com");
        User updated = service.updateUser(created);
        System.out.println("Обновленный email: " + updated.getEmail());

        boolean deleted = service.deleteUser(created.getId());
        System.out.println("Удалён: " + deleted);

        service.saveCommentsOfLastPost(1);

        service.printOpenTodos(1);
    }
}