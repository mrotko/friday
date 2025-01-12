package pl.rotkom.friday.thirdparty.google.oauth2;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

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
import lombok.extern.slf4j.Slf4j;
import pl.rotkom.friday.library.files.FileService;
import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.common.GoogleCommons;


@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOauth2Manager {

    private static final String CREDENTIALS_NAME = "StoredCredential";

    private final GoogleConfig config;

    private final FileService fileService;

    @SneakyThrows
    private void loadCredentials() {
        if (config.getCredentials() == null) {
            log.info("Google oauth2 credentials are not set");
            return;
        }
        var directory = fileService.getFilesDir();
        var credentialsPath = directory.resolve(CREDENTIALS_NAME);
        if (Files.exists(credentialsPath)) {
            log.info("Credentials already exist in directory: {}", directory);
            return;
        }
        var credentials = Base64.getDecoder().decode(config.getCredentials());
        Files.write(credentialsPath, credentials, StandardOpenOption.CREATE);

        log.info("Credentials loaded to directory: {}", directory);
    }

    // only for development purposes
    @SneakyThrows
    private void exportCredentials() {
        var directory = fileService.getFilesDir();
        var bytes = Files.readAllBytes(directory.resolve(CREDENTIALS_NAME));
        var encoded = Base64.getEncoder().encodeToString(bytes);
        var output = directory.resolve(CREDENTIALS_NAME + "-base64.txt");
        if(Files.exists(output)) {
            log.info("Deleting existing file: {}", output);
            Files.delete(output);
        }
        Files.writeString(output, encoded, StandardOpenOption.CREATE);
        log.info("Credentials exported to file: {}", output);
    }

    @SneakyThrows
    public Credential getCredentials() {
        loadCredentials();

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
                .setDataStoreFactory(new FileDataStoreFactory(fileService.getFilesDir().toFile()))
                .build();

        var receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        var credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("resource_owner_email");

//        exportCredentials(); // only for development purposes

        return credential;
    }
}
