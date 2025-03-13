Code FE lấy dữ liệu từ firebase (dùng MQTT lấy dữ liệu từ adafruit rồi lưu vào firebase có tác dụng cho app cập nhật theo thời gian thực mà không cần polling
đồng thời có tác dụng tốt cho việc phát triển ứng dụng)

## API Endpoints

- **GET /api/adafruit/feeds**  
  Trả về danh sách các thiết bị đang có trên Adafruit.

- **GET /api/adafruit/feeds/{feedKey}**  
  Xem dữ liệu mới nhất của thiết bị với `feedKey`.

- **POST /api/adafruit/fetch/{feedKey}**  
  Lưu dữ liệu từ Adafruit vào PostgreSQL & Firebase.

- **GET /api/adafruit/data**  
  Xem danh sách dữ liệu đã lưu trong PostgreSQL.

- **POST /api/adafruit/sync**  
  Cập nhật dữ liệu từ Adafruit vào Firebase ngay lập tức (thay vì chờ cập nhật).
