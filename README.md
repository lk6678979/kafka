# kafka
## 1.创建所需ssl证书文件
### 脚本
```shell
#!/bin/bash
#################################
BASE_DIR=/data/kafka-ssl
CERT_OUTPUT_PATH="$BASE_DIR/server_cert"
CLIENT_CERT_OUTPUT_PATH="$BASE_DIR/client_cert"
PASSWORD=123456
KEY_STORE="$CERT_OUTPUT_PATH/kafka.keystore.jks"
CLIENT_KEY_STORE="$CLIENT_CERT_OUTPUT_PATH/client-keystore.jks"
TRUST_STORE="$CERT_OUTPUT_PATH/kafka.truststore.jks"
CLIENT_TRUST_STORE="$CLIENT_CERT_OUTPUT_PATH/client-truststore.jks"
CLUSTER_NAME=test
CERT_AUTH_FILE="$CERT_OUTPUT_PATH/ca.crt"
CLUSTER_CERT_FILE="$CERT_OUTPUT_PATH/${CLUSTER_NAME}-cert"
CLIENT_CLUSTER_CERT_FILE="$CERT_OUTPUT_PATH/${CLUSTER_NAME}-cert-client"
DAYS_VALID=999
DNAME="CN=test,OU=test,O=test,L=test,ST=test,C=CN"
#################################
mkdir -p $CERT_OUTPUT_PATH
mkdir -p $CLIENT_CERT_OUTPUT_PATH

echo "1:创建服务端秘钥和证书"
keytool -keystore $KEY_STORE -alias $CLUSTER_NAME -validity $DAYS_VALID -genkey -keyalg RSA \
-storepass $PASSWORD -keypass $PASSWORD -dname "$DNAME"

echo "2:创建客户端秘钥和证书"
keytool -keystore $CLIENT_KEY_STORE -alias $CLUSTER_NAME -validity $DAYS_VALID -genkey -keyalg RSA \
-storepass $PASSWORD -keypass $PASSWORD -dname "$DNAME"

echo "3:创建CA证书"
openssl req -new -x509 -keyout $CERT_OUTPUT_PATH/ca.key -out "$CERT_AUTH_FILE" -days "$DAYS_VALID" \
-passin pass:"$PASSWORD" -passout pass:"$PASSWORD" \
-subj "/C=CN/ST=test/L=test/O=test/CN=CN"

echo "4:将CA证书导入到服务器truststore"
keytool -keystore "$TRUST_STORE" -alias CARoot \
-import -file "$CERT_AUTH_FILE" -storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "5:将CA证书导入到客户端truststore"
keytool -keystore "$CLIENT_TRUST_STORE" -alias CARoot \
-import -file "$CERT_AUTH_FILE" -storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "6.导出服务器证书"
keytool -keystore "$KEY_STORE" -alias "$CLUSTER_NAME" -certreq -file "$CLUSTER_CERT_FILE" \
-storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "7.导出客户器证书"
keytool -keystore "$CLIENT_KEY_STORE" -alias "$CLUSTER_NAME" -certreq -file "$CLIENT_CLUSTER_CERT_FILE" \
-storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "8.用CA证书给服务器证书签名"
openssl x509 -req -CA "$CERT_AUTH_FILE" -CAkey $CERT_OUTPUT_PATH/ca.key -in "$CLUSTER_CERT_FILE" \
-out "${CLUSTER_CERT_FILE}-signed" \
-days "$DAYS_VALID" -CAcreateserial -passin pass:"$PASSWORD"

echo "8.用CA证书给客户器证书签名"
openssl x509 -req -CA "$CERT_AUTH_FILE" -CAkey $CERT_OUTPUT_PATH/ca.key -in "$CLIENT_CLUSTER_CERT_FILE" \
-out "${CLIENT_CLUSTER_CERT_FILE}-signed" \
-days "$DAYS_VALID" -CAcreateserial -passin pass:"$PASSWORD"

echo "9.将CA证书导入服务端keystore"
keytool -keystore "$KEY_STORE" -alias CARoot -import -file "$CERT_AUTH_FILE" -storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "10.将CA证书导入客户端keystore"
keytool -keystore "$CLIENT_KEY_STORE" -alias CARoot -import -file "$CERT_AUTH_FILE" -storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "11:将已签名的服务端证书导入服务器keystore"
keytool -keystore "$KEY_STORE" -alias "${CLUSTER_NAME}" -import -file "${CLUSTER_CERT_FILE}-signed" \
-storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt

echo "11:将已签名的客户端证书导入服务器keystore"
keytool -keystore "$CLIENT_KEY_STORE" -alias "${CLUSTER_NAME}" -import -file "${CLIENT_CLUSTER_CERT_FILE}-signed" \
-storepass "$PASSWORD" -keypass "$PASSWORD" -noprompt
```
创建的文件清单：
* 服务端文件
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-1.jpg)
* 客户端文件
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-2.jpg)
## 2.服务端配置
### 2.1 配置ssl监听
Kafka Broker支持监听多个端口上的连接，通过server.properteis 配置，最少监听1个端口，用逗号分隔。
```
listeners=PLAINTEXT://host.name:port,SSL://host.name:port
```
* CDH对应配置
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-3.png)
### 2.2 配置ssl文件和密码
```
ssl.keystore.location=/data/kafka-ssl/server_cert/kafka.keystore.jks
ssl.keystore.password=test1234
ssl.key.password=test1234
ssl.truststore.location=/data/kafka-ssl/server_cert/kafka.truststore.jks
ssl.truststore.password=test1234
```
* CDH对应配置
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-4.png)
### 2.2 ssl其他配置
### 2.2.1 ssl.client.auth
* required：客户端身份验证是必需的，客户端必须同时配置keystore和truststore
* node：不启动验证
* requested：客户端身份验证请求，客户端没有证书仍然可以连接，客户端连接Kafka只用配置truststore
* CDH配置：
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-5.png)
### 2.2.2. ssl.enabled.protocols = TLSv1.2 TLSv1.1 TLSv1 
* TLSv1.2 TLSv1.1 TLSv1 ，接收来自客户端列出的SSL协议，注意，不推荐在生产中使用SSL，推荐使用TLS)。
* CDH不能配置具体协议，只能选择是否启动
![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-6.png)
### 2.2.3. security.inter.broker.protocol 
* broker内部通信使用协议，默认和外部通讯协议保持一致，要么保护默认配置或者改为SSL
 ![](https://github.com/lk6678979/image/blob/master/kafka-ssl/kakfa-ssh-7.jpg)
