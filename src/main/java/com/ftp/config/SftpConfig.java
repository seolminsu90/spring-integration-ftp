package com.ftp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@Configuration
public class SftpConfig {
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.port:22}")
    private int sftpPort;
    @Value("${sftp.user}")
    private String sftpUser;
    @Value("${sftp.password}")
    private String sftpPasword;
    @Value("${sftp.remote:/data}")
    private String sftpRemote;

    @Bean
    public SessionFactory<LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(sftpHost);
        factory.setPort(sftpPort);
        factory.setUser(sftpUser);
        factory.setPassword(sftpPasword);
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }

    @Bean(name = "sftpTemplate")
    public SftpRemoteFileTemplate sftpRemoteFileTemplate() throws Exception {
        SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(sftpSessionFactory());
        template.setRemoteDirectoryExpression(new LiteralExpression(sftpRemote));
        template.afterPropertiesSet();
        return template;
    }
}
