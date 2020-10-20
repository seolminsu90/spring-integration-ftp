# spring-integration-ftp
spring integration ftp

- 스프링 Integration을 통해 ftp upload / download 구현.
- 기존엔 AWS S3 등의 클라우드 기반으로만 해봐서 FTP 활용한 업로드 구상이 필요했음.

### FTP 대상 서버 설정

#### 유저 생성 및 데이터 폴더 설정
```bash
sudo su
add group [groupname]
useradd -m [membername] -g [groupname]
passwd [membername]
mkdir [ftp data repository path..]
chmod 755 -R [ftp data repository path]
chown [membername] [ftp data repository path]
```
#### 파일 설정 변경
```bash
vi /etc/ssh/sshd_config

# Subsystem sftp /user/lib/openssh/sftp-server --- 기존 내용 주석 처리
Subsystem sftp internal-sftp
Match user [membername]
ChrootDirectory [ftp data repository path]
ForceCommand internal-sftp
AllowTcpForwarding no
```

#### 테스트
```bash
sftp [membername]@[serverhost]
```
