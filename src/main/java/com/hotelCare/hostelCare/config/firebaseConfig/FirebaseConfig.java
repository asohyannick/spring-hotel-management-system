package com.hotelCare.hostelCare.config.firebaseConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Value("${firebase.private-key-id}")
    private String privateKeyId;

    @Value("${firebase.client-id}")
    private String clientId;

    @Value("${firebase.auth-uri}")
    private String authUri;

    @Value("${firebase.token-uri}")
    private String tokenUri;

    @Value("${firebase.auth-provider-x509-cert-url}")
    private String authProviderCertUrl;

    @Value("${firebase.client-x509-cert-url}")
    private String clientCertUrl;

    @PostConstruct
    public void initializeFirebase() {
        try {
            String formattedPrivateKey = privateKey
                    .replace("\\n", "\n")
                    .trim();

            if (!formattedPrivateKey.contains("BEGIN PRIVATE KEY")
                    || !formattedPrivateKey.contains("END PRIVATE KEY")) {
                throw new IllegalStateException("Invalid Firebase private key format");
            }

            String firebaseJson = """
                {
                  "type": "service_account",
                  "project_id": "%s",
                  "private_key_id": "%s",
                  "private_key": "%s",
                  "client_email": "%s",
                  "client_id": "%s",
                  "auth_uri": "%s",
                  "token_uri": "%s",
                  "auth_provider_x509_cert_url": "%s",
                  "client_x509_cert_url": "%s"
                }
                """.formatted(
                    projectId,
                    privateKeyId,
                    formattedPrivateKey.replace("\n", "\\n"),
                    clientEmail,
                    clientId,
                    authUri,
                    tokenUri,
                    authProviderCertUrl,
                    clientCertUrl
            );

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(
                            firebaseJson.getBytes(StandardCharsets.UTF_8)))
                    .createScoped(List.of(
                            "https://www.googleapis.com/auth/cloud-platform"
                    ));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception ex) {
            throw new IllegalStateException("🔥 Firebase initialization failed", ex);
        }
    }
}
