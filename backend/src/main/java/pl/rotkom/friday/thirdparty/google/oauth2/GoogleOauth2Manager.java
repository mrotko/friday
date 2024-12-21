package pl.rotkom.friday.thirdparty.google.oauth2;

import java.io.File;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.util.store.FileDataStoreFactory;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.common.GoogleCommons;


@Component
@RequiredArgsConstructor
public class GoogleOauth2Manager {

    private final GoogleConfig config;

    @SneakyThrows
    private void loadCredentials() {

    }

    @SneakyThrows
    public Credential getCredentials() {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        var installed = new Details();
        installed.setClientId(config.getClientId());
        installed.setAuthUri(config.getAuthUri());
        installed.setTokenUri(config.getTokenUri());
        installed.setClientSecret(config.getClientSecret());
        installed.setRedirectUris(config.getRedirectUris());

        clientSecrets.setInstalled(installed);

        var flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleCommons.HTTP_TRANSPORT, GoogleCommons.JSON_FACTORY, clientSecrets, config.getScopes())
                .setAccessType("offline")
                .setApprovalPrompt("auto")
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .build();

        var receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("resource_owner_email");
    }
}
