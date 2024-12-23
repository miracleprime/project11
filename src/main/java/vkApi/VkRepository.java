package vkApi;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.users.UsersSearchQuery;
import com.vk.api.sdk.queries.users.UsersGetQuery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VkRepository {

    private final int APP_ID;
    private final String CODE ;
    private final VkApiClient vk;
    private final UserActor actor;

    public VkRepository(int appId, String code) {
        this.APP_ID = appId;
        this.CODE = code;

        TransportClient transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
        actor = new UserActor(APP_ID, CODE);
    }

    public List<?> getUsersByFirstNameLastName(String firstName, String lastName) throws ApiException, ClientException {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Имя и фамилия не могут быть пустыми или null.");
        }

        try {
            UsersSearchQuery query = vk.users().search(actor)
                    .q(firstName + " " + lastName)
                    .count(5);

            List<UserFull> users = query.execute().getItems();

            return users != null ? users : Collections.emptyList();

        } catch (ApiException | ClientException e) {
            System.err.println("Ошибка при получении данных: " + e.getMessage());
            throw e;
        }
    }

    public List<UserFull> getUsersByName(String name) throws ApiException, ClientException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым или null.");
        }

        try {
            UsersSearchQuery query = vk.users().search(actor)
                    .q(name)
                    .count(50);

            List<UserFull> users = query.execute().getItems();

            return users != null ? users : Collections.emptyList();
        } catch (ApiException | ClientException e) {
            System.err.println("Ошибка при получении данных: " + e.getMessage());
            throw e;
        }
    }

    public List<? extends Object> getUsersByIds(List<Integer> ids) throws ApiException, ClientException {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Список ID не может быть пустым или null.");
        }

        try {
            UsersGetQuery query = vk.users().get(actor)
                    .userIds(ids.stream().map(Object::toString).collect(Collectors.joining(",")));

            List<GetResponse> users = query.execute();

            return users != null ? users : Collections.emptyList();
        } catch (ApiException | ClientException e) {
            System.err.println("Ошибка при получении данных по ID: " + e.getMessage());
            throw e;
        }
    }

}