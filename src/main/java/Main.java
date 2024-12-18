import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserFull;
import vkApi.*;
import com.vk.api.sdk.objects.users.User;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int appId = 6;
        String code = "";
        try {
            VkRepository vkRepository = new VkRepository(appId, code);

            // Example usage (corrected type handling)
            List<User> usersByFullName = (List<User>) vkRepository.getUsersByFirstNameLastName("Иван", "Иванов");
            if (!usersByFullName.isEmpty()) {
                System.out.println("Пользователи по имени и фамилии:");
                for (User user : usersByFullName) {
                    System.out.println("ID: " + user.getId() + ", Имя: " + user.getFirstName() + " " + user.getLastName());
                }
            } else {
                System.out.println("Пользователи с таким именем и фамилией не найдены.");
            }

            List<UserFull> usersByName = vkRepository.getUsersByName("Иван");
            if (!usersByName.isEmpty()) {
                System.out.println("\nПользователи по имени:");
                for (UserFull user : usersByName) { // Now correct type UserFull
                    System.out.println("ID: " + user.getId() + ", Имя: " + user.getFirstName() + " " + user.getLastName());
                }
            } else {
                System.out.println("Пользователи с таким именем не найдены.");
            }

            List<Integer> userIds = Arrays.asList(1, 2, 3);
            //Crucially important change
            if (userIds != null && !userIds.isEmpty()) {
                List<User> usersByIds = (List<User>) vkRepository.getUsersByIds(userIds);
                if (!usersByIds.isEmpty()) {
                    System.out.println("\nПользователи по ID:");
                    for (User user : usersByIds) {
                        System.out.println("ID: " + user.getId() + ", Имя: " + user.getFirstName() + " " + user.getLastName());
                    }
                } else {
                    System.out.println("Пользователи с такими ID не найдены.");
                }
            } else {
                System.out.println("Список ID пустой или null");
            }


        } catch (ApiException | ClientException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}