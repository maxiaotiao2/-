package com.smilecat.util;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.*;

@Component
public class EmailUtil {
    // 从application.yml配置文件中获取 // 发送发邮箱地址
    @Value("${spring.mail.from}")
    private String from;


    @Autowired
    private JavaMailSender mailSender;

    public void sendMessageCarryFile(String to, String subject, String content, XSSFWorkbook wb) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();


        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            // 设置发送发
            helper.setFrom(from);
            // 设置接收方
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content);
            try {
                // 创建临时文件
                File tempFile = File.createTempFile("temp", ".xlsx");

                // 将 XSSFWorkbook 写入临时文件
                try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
                    wb.write(fileOut);
                }

                // 将临时文件作为附件添加到邮件中
                helper.addAttachment(tempFile.getName(), tempFile);
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }

}
