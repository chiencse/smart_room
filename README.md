﻿Code FE lấy dữ liệu từ firebase (dùng MQTT lấy dữ liệu từ adafruit rồi lưu vào firebase có tác dụng cho app cập nhật theo thời gian thực mà không cần polling
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

- **GET /api/activity/logs**
Xem lịch sử thao tác trong PostgreSQL (do chỉ mới có 1 thiết bị nên không lưu deviceId mà lưu deviceKey tạm, có api xem của userId,deiceKey nếu cần)

- **POST /api/adafruit/sync**  
  Cập nhật dữ liệu từ Adafruit vào Firebase ngay lập tức (thay vì chờ cập nhật).

