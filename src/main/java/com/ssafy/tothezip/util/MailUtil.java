package com.ssafy.tothezip.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
@Slf4j
public class MailUtil {
    @Value("${mail.smtp.username:}")
    private String username;

    @Value("${mail.smtp.password:}")
    private String password; // 중요: Google '앱' 비밀번호

    @Value("${mail.smtp.host:smtp.gmail.com}")
    private String host;

    @Value("${mail.smtp.port:587}")
    private String port;

    @Value("${mail.smtp.auth:true}")
    private String auth;

    @Value("${mail.smtp.starttls.enable:true}")
    private String starttls;

    /**
     * 인증 "번호" 발송 메소드
     *
     * @param toEmail 수신자 이메일
     * @param code    6자리 인증 코드
     */
    public void sendVerificationCode(String toEmail, String code) {

        /// 디버깅 ///
//        System.out.println("username: " + username + " password: " + password);
        ///////

        log.info("mail username loaded? {}", (username != null && !username.isBlank()));

        if (username == null || password == null) {
            System.err.println("이메일 설정이 로드되지 않아 메일을 발송할 수 없습니다.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // password는 config.properties에서 로드한 16자리 "앱 비밀번호"
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "ToTheZip"));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[ToTheZip] 회원가입 인증 코드입니다.");

            // 이메일 내용을 인증 "번호"로 변경
            String emailContent = String.format(
                    "<h1>ToTheZip 회원가입을 환영합니다!</h1>" +
                            "<p>회원가입을 완료하려면 아래 6자리 인증번호를 입력해주세요.</p>" +
                            "<div style='font-size: 24px; font-weight: bold; padding: 10px; background: #f4f4f4;'>" +
                            "%s" +
                            "</div>",
                    code // 6자리 인증번호
            );

            /// 디버깅 ///
//            System.out.println("code: " + code);
            //////
            message.setContent(emailContent, "text/html; charset=utf-8");

            Transport.send(message);
            /// 디버깅 ///
//            System.out.println("인증 메일 발송 성공: " + toEmail);
            //////

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("인증 메일 발송 실패: " + toEmail);
            // 인증 실패 시 여기서 AuthenticationFailedException 발생 가능
            if (e instanceof AuthenticationFailedException) {
                System.err.println("!!!!! 인증 실패 !!!!! Google 앱 비밀번호가 올바른지 확인하세요.");
            }
        }
    }
}