-- Tạo bảng User
CREATE TABLE "User" (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    date_of_birth DATE,
    role VARCHAR(50)
);

-- Tạo bảng Device
CREATE TABLE "Device" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    power INT DEFAULT 0, -- 0-100 cho fan, ON/OFF cho lamp/door
    sensor_id INT,
    FOREIGN KEY (sensor_id) REFERENCES "Sensor"(id) ON DELETE SET NULL
);

-- Tạo bảng Sensor
CREATE TABLE "Sensor" (
    id SERIAL PRIMARY KEY,
    type VARCHAR(100) NOT NULL
);

-- Tạo bảng Env_param (Lưu dữ liệu cảm biến)
CREATE TABLE "Env_param" (
    id SERIAL PRIMARY KEY,
    time TIMESTAMP DEFAULT NOW(),
    value FLOAT NOT NULL,
    sensor_id INT NOT NULL,
    FOREIGN KEY (sensor_id) REFERENCES "Sensor"(id) ON DELETE CASCADE
);

-- Tạo bảng Scheduler (Lập lịch cho thiết bị)
CREATE TABLE "Scheduler" (
    id SERIAL PRIMARY KEY,
    time TIMESTAMP NOT NULL,
    action VARCHAR(50) NOT NULL, -- ON/OFF hoặc mức 0-100 với quạt
    user_id INT NOT NULL,
    device_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "User"(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES "Device"(id) ON DELETE CASCADE
);

-- Tạo bảng Log_activity (Ghi nhận hoạt động)
CREATE TABLE "Log_activity" (
    id SERIAL PRIMARY KEY,
    time TIMESTAMP DEFAULT NOW(),
    type VARCHAR(50) NOT NULL, -- Loại hành động
    power INT DEFAULT 0, -- Trạng thái thiết bị (0-100 hoặc ON/OFF)
    user_id INT,
    device_id INT,
    FOREIGN KEY (user_id) REFERENCES "User"(id) ON DELETE SET NULL,
    FOREIGN KEY (device_id) REFERENCES "Device"(id) ON DELETE CASCADE
);
