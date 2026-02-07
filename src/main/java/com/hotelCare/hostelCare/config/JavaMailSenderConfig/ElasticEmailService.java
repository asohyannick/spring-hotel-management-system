package com.hotelCare.hostelCare.config.JavaMailSenderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ElasticEmailService {

    @Value("${elastic.api-key}")
    private String apiKey;

    @Value("${elastic.from-email}")
    private String fromEmail;

    @Value("${elastic.from-name}")
    private String fromName;

    private final RestTemplate restTemplate;

    public ElasticEmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void send2FACodeEmail(String toEmail, String code) {
        sendEmail(toEmail, "HostelCare Account Verification: Your 2FA Security Code", build2FAHtmlContent(code));
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetCode) {
        sendEmail(toEmail, "Password Reset Verification Code", buildPasswordResetHtmlContent(resetCode));
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        String url = "https://api.elasticemail.com/v4/emails/transactional";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-ElasticEmail-ApiKey", apiKey);

            Map<String, Object> emailPayload = buildEmailPayload(toEmail, subject, htmlContent);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(url),  // Use URI.create() instead of passing String
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to: {} with subject: {}", toEmail, subject);
            } else {
                log.error("Elastic Email failed: {} {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private Map<String, Object> buildEmailPayload(String toEmail, String subject, String htmlContent) {
        Map<String, Object> payload = new HashMap<>();

        Map<String, Object> recipients = new HashMap<>();
        recipients.put("To", new String[]{toEmail});
        payload.put("Recipients", recipients);

        Map<String, Object> content = new HashMap<>();
        content.put("From", fromEmail);
        content.put("FromName", fromName);
        content.put("Subject", subject);

        // Email body
        Map<String, String> body = new HashMap<>();
        body.put("ContentType", "HTML");
        body.put("Charset", "utf-8");
        body.put("Content", htmlContent);

        content.put("Body", new Object[]{body});
        payload.put("Content", content);

        return payload;
    }

    private String build2FAHtmlContent(String code) {
        return """
            <div style="margin:0;padding:0;background-color:#f5f7fb;font-family:Arial,Helvetica,sans-serif;">
              <div style="max-width:640px;margin:30px auto;background:#ffffff;
                          border:1px solid #e5e7eb;border-radius:12px;
                          box-shadow:0 6px 18px rgba(17,24,39,0.06);overflow:hidden;">

                <div style="padding:22px 24px;
                            background:linear-gradient(135deg,#0f172a,#1d4ed8);
                            color:#ffffff;">
                  <h2 style="margin:0;font-size:18px;">HostelCare Security Team</h2>
                  <p style="margin:6px 0 0;font-size:13px;opacity:0.9;">
                    Two-Factor Authentication (2FA)
                  </p>
                </div>

                <div style="padding:24px;color:#1f2937;font-size:14px;line-height:1.6;">
                  <p>Dear customer,</p>

                  <p>Please use the following verification code:</p>

                  <div style="margin:20px 0;padding:18px;
                              background:#eff6ff;border:1px solid #bfdbfe;
                              border-radius:12px;text-align:center;">
                    <div style="font-size:28px;font-weight:800;letter-spacing:6px;color:#1e40af;">
                      %s
                    </div>
                  </div>

                  <p>This code expires in <strong>5 minutes</strong>.</p>

                  <p>
                    Sincerely,<br/>
                    <strong>The HostelCare Team</strong>
                  </p>
                </div>

              </div>
            </div>
            """.formatted(code);
    }

    private String buildPasswordResetHtmlContent(String resetCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>Password Reset</title>
            </head>
            <body style="margin:0;padding:0;background:#f6f9fc;font-family:Arial,Helvetica,sans-serif;">
              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f6f9fc;padding:24px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="600" cellspacing="0" cellpadding="0"
                           style="width:600px;max-width:92%%;background:#ffffff;border-radius:12px;overflow:hidden;
                                  box-shadow:0 6px 18px rgba(0,0,0,0.08);">
                      <tr>
                        <td style="background:#0b5ed7;padding:18px 24px;color:#ffffff;">
                          <h2 style="margin:0;font-size:18px;line-height:1.4;">Password Reset Verification</h2>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:24px;color:#1f2937;">
                          <p style="margin:0 0 14px;font-size:14px;line-height:1.6;">
                            Hi there, 👋
                          </p>

                          <p style="margin:0 0 16px;font-size:14px;line-height:1.6;">
                            We received a request to reset your password. Use the verification code below to continue:
                          </p>

                          <div style="text-align:center;margin:22px 0;">
                            <span style="display:inline-block;background:#f3f4f6;border:1px solid #e5e7eb;
                                         padding:14px 20px;border-radius:10px;font-size:28px;letter-spacing:6px;
                                         font-weight:700;color:#111827;">
                              %s
                            </span>
                          </div>

                          <p style="margin:0 0 10px;font-size:13px;line-height:1.6;color:#374151;">
                            ⏳ This code will expire in <strong>15 minutes</strong>.
                          </p>

                          <p style="margin:0;font-size:13px;line-height:1.6;color:#6b7280;">
                            If you didn't request this, you can safely ignore this email.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:16px 24px;background:#f9fafb;border-top:1px solid #e5e7eb;color:#6b7280;">
                          <p style="margin:0;font-size:12px;line-height:1.5;">
                            © %d HostelCare. All rights reserved.
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(resetCode, java.time.Year.now().getValue());
    }
}