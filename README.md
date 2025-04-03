Code FE lấy dữ liệu từ firebase (dùng MQTT lấy dữ liệu từ adafruit rồi lưu vào firebase có tác dụng cho app cập nhật theo thời gian thực mà không cần polling
đồng thời có tác dụng tốt cho việc phát triển ứng dụng)

Kết hợp Firebase + PostgreSQL
Ví dụ:

Firebase để xử lý realtime, authentication, notifications.
PostgreSQL để lưu trữ dữ liệu chính, báo cáo, phân tích dữ liệu.
Kết hợp Cloud Functions để đồng bộ dữ liệu giữa hai bên.

Bạn đang sử dụng Adafruit IO để thu thập dữ liệu từ IoT devices, sau đó lưu vào Firebase, rồi từ Firebase đẩy lên Frontend (FE).

🔍 Ưu điểm của cách này
Firebase Realtime Database giúp FE có thể lắng nghe dữ liệu mới ngay lập tức mà không cần liên tục gửi request.
Firebase có cơ chế cache → Nếu thiết bị mất mạng, dữ liệu vẫn có thể truy cập được khi có lại kết nối.
Dễ dàng mở rộng → Firebase có thể lưu nhiều loại dữ liệu khác nhau, có thể tích hợp với Push Notifications để gửi cảnh báo.
Phù hợp với kiến trúc Serverless → Không cần viết server backend phức tạp, mọi thứ xử lý trực tiếp trên Firebase.
🤔 Có nên bỏ qua Firebase và đọc trực tiếp từ Adafruit lên FE?
📌 Cách này vẫn có thể làm, nhưng có một số hạn chế:

Không có lịch sử dữ liệu nếu chỉ lấy trực tiếp từ Adafruit. Firebase có thể giúp lưu trữ lâu dài.
Không có khả năng xử lý dữ liệu phức tạp → Nếu cần lọc dữ liệu, tính toán, hoặc cảnh báo tự động, Firebase hỗ trợ tốt hơn.
Phải liên tục subscribe MQTT từ FE → Nếu bạn để FE kết nối thẳng MQTT, thì mỗi client sẽ tạo một kết nối riêng, có thể gây tải nặng khi nhiều người dùng cùng lúc.

## API Endpoints

- **GET /api/adafruit/feeds**  
  Trả về danh sách các thiết bị đang có trên Adafruit.

- **GET /api/adafruit/feeds/{feedKey}**  
  Xem dữ liệu mới nhất của thiết bị với `feedKey`.

- **POST /api/adafruit/fetch/{feedKey}**  
  Lưu dữ liệu từ Adafruit vào PostgreSQL & Firebase.

- **GET /api/adafruit/data**  
  Xem danh sách dữ liệu đã lưu trong PostgreSQL.

**GET /api/activity/logs**
Xem lịch sử thao tác trong PostgreSQL (do chỉ mới có 1 thiết bị nên không lưu deviceId mà lưu deviceKey tạm, có api xem của userId,deiceKey nếu cần)

- **POST /api/adafruit/sync**  
  Cập nhật dữ liệu từ Adafruit vào Firebase ngay lập tức (thay vì chờ cập nhật).

spring.application.name=smart_room

spring.datasource.url=jdbc:postgresql://localhost:5432/smart_room
spring.datasource.username=postgres
spring.datasource.password=tan01012004
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
#spring.jpa.hibernate.ddl-auto=update

# Hibernate Configuration

spring.jpa.database=postgresql
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

adafruit.api.base-url=https://io.adafruit.com/api/v2/QUOCAN28
logging.level.org.springframework.web.client.RestTemplate=DEBUG
adafruit.api.key=aio_DsrM82XLFJJ3G2R1nXHcKRFRXDn9

adafruit.mqtt.broker=tcp://io.adafruit.com:1883
adafruit.mqtt.username=QUOCAN28
adafruit.mqtt.key=aio_DsrM82XLFJJ3G2R1nXHcKRFRXDn9
adafruit.mqtt.feeds=temp,humidity,air,device.door,device.status-fan,device.status-lamp,light,device.lamp,device.fan

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

{
"type": "service_account",
"project_id": "smarthouse-6b26c",
"private_key_id": "2523717efd47418cfdfa7c51e022c0027cad18bb",
"private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQChau+woNYzJhDx\nlyHQYFZui9gZGQAjnbRmVH0zwMRAvFydDw2KvN1chyzw+C0+tHYH8unhtkQ1+SAF\ngmnoPozYe6J+4MPALgzayaaor8+GpAFxrhVJO0syo3To3jPfC6+h4OTXdL3c8vLT\nuJ4mc6xl6gV5+B3UXhkKNPGBhQhFymQoUjNDwfijIdQEHbcdEjRC4Xbu5GhQx1qy\nkyXWOlA9h3Wy9qTdsNcywbeZIW/imKvOlVnMdXsi9zBJcWm6JDMjYIIlnzw8pjoo\nQDC3aqPKdsy6+5kazgP7tw84osVmIeo8K9GKdswFqGFVEp/HsdmFHJwO5+Y3vIlh\nPIkOOnT9AgMBAAECggEADZQaT6hn/GuCV2BYRtgjzS7UYeAZg+V7THRlB9p6Z9uw\nisG+aOJUWLlCTY8C91hk6WxEwmj7ufKX//6lfBTu8jMMS1ILBXqaODloXJ5VfcUE\nHhT/02Y6gCsB5ajzghrQjnSFq541boW9aZQJMcDfSr9x8H2vvLKhtpsXicawGHa4\nyWewDNmMpWC5DmRT8RltYsbzlG7ringRjobctSOnkDJYAb/+IJzlxRazuz4aJ/LA\nJC4HSk7qWBm0aNVyI2MLTqu8Vv8KFBb7Gt/SU1OJCNJuMhkf9Xiv7o0hMhfdxNSe\n5Dn+owZ8xPk0K6/jKkE5PM6K1F5qWva+9y2/37CBYQKBgQDZSAwSLnyx5Wxz6hC7\nvQsi2WlaBGRMFzlfBf1l9sFAKxPxE4qqrf4+e6C4rcyzX7isvg/mTIv9av7XYHtz\nhDnCFogjnsTVjSYxnzHuA00pqbqzeRrIn0CMMc5w1PeE7LJWc16zGM5oNW3J/AAd\n2EXREa8jvacA/MldncE9Y5R1BQKBgQC+Ln796AOvpIXxw+UIIAWnBLAk7nh++blC\njcdU+leklJGBjbrUr0FeWKghy4fALWQl7zgRRcPZDpIGmddQxJqt5OiTJp7p3ELq\nODbtz+/EgFMDjB0tmt3gNiW9ZeUd/Jk+NeJMyS7sc8bSAFS7Urrbo5d2+ZJPXVqi\nCfAUM7WBmQKBgQCb8ygmzJLXJDGpKLCF7vNVOfJxl4FdU7Xry+LKEkc2BTU07K+0\nPVmkAxuiMawgem2UHn5O71Xyt57devRgPuEtuvpPhp8EH2DwcLBngpsZcib8tclR\ngWvUs9LWDSTylOiTQA8SQJW1GdQJCwQSZ64Elsq7vl1lSgpFVPrjUtu0+QKBgH/M\nrvng19lILdpxUY9MNYAu3k9zEPYCykO3EqRANnUagU0V+N7DhqHn4dQT+X1jA1Ga\nl4rD3wAVknttFgvokikZElEQOuncYfsTG5ZjjTP3J9HSbikfUMpCmMnVl0m3XSM5\nAoGnEClFA+5K2qWmynqeTEJfI7rBeQ46r2Jj/SzBAoGBAKhHeg2I+oNppQfSXNAp\n091o2OJ2uwrXZDozU2izFUZx+eYrDKjct/8xQftG9MFMF5RPlK5KAltcfCrI/Rb2\nIH8f8Azlw43Gak2Xty9lfxw5yaMlHs0/iMLt+T++b01XjCcL1TJxJ11dAchZ+2EN\nmYg5ujVUw3Slevz7Hp/ADxmE\n-----END PRIVATE KEY-----\n",
"client_email": "firebase-adminsdk-fbsvc@smarthouse-6b26c.iam.gserviceaccount.com",
"client_id": "115104639255283911306",
"auth_uri": "https://accounts.google.com/o/oauth2/auth",
"token_uri": "https://oauth2.googleapis.com/token",
"auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
"client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40smarthouse-6b26c.iam.gserviceaccount.com",
"universe_domain": "googleapis.com"
}
