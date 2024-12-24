package vkApi;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserFull;
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

    public List<User> getUsersByFirstNameLastName(String firstName, String lastName) throws ApiException, ClientException {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Имя и фамилия не могут быть пустыми или null.");
        }
        try {
            UsersSearchQuery query = vk.users().search(actor)
                    .q(firstName + " " + lastName)
                    .count(5);
            List<UserFull> usersFull = query.execute().getItems();
            if (usersFull == null || usersFull.isEmpty()) {
                return Collections.emptyList();
            }
            return  usersFull.stream()
                    .map(userFull -> (User) userFull)
                    .collect(Collectors.toList());
        } catch (ApiException | ClientException e) {
            throw e;
        }
    }
    public List<User> getUsersByName(String name) throws ApiException, ClientException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым или null.");
        }

        try {
            UsersSearchQuery query = vk.users().search(actor)
                    .q(name)
                    .count(50);
            List<UserFull> usersFull = query.execute().getItems();
            if (usersFull == null || usersFull.isEmpty()) {
                return Collections.emptyList();
            }

            return  usersFull.stream()
                    .map(userFull -> (User) userFull)
                    .collect(Collectors.toList());
        } catch (ApiException | ClientException e) {
            throw e;
        }
    }
    public List<User> getUsersByIds(List<Integer> ids) throws ApiException, ClientException {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Список ID не может быть пустым или null.");
        }
        try {
            UsersGetQuery query = vk.users().get(actor)
                    .userIds(ids.stream().map(Object::toString).collect(Collectors.joining(",")));
            return query.execute().stream()
                    .map(response -> (User) response)
                    .collect(Collectors.toList());
        } catch (ApiException | ClientException e) {
            throw e;
        }
    }
}
