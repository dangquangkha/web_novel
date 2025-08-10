-- 1.1. Database
CREATE DATABASE webnovel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webnovel;

-- 1.2. Bảng users (cho Login/Register)
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('READER','AUTHOR','ADMIN') NOT NULL DEFAULT 'READER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1.3. Bảng novels
CREATE TABLE novels (
  id INT AUTO_INCREMENT PRIMARY KEY,
  author_id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  other_names VARCHAR(200),
  is_sensitive BOOLEAN NOT NULL DEFAULT FALSE,
  cover_path VARCHAR(255),          -- đường dẫn file image trên server
  genre VARCHAR(100),
  status ENUM('ONGOING','COMPLETED') NOT NULL DEFAULT 'ONGOING',
  is_public BOOLEAN NOT NULL DEFAULT TRUE,
  summary TEXT,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 1.4. Bảng donations
CREATE TABLE donations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  novel_id INT NOT NULL,
  donor_id INT,                     -- NULL nếu donate ẩn danh
  amount DECIMAL(10,2) NOT NULL,
  donated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE,
  FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE SET NULL
);


-- 1.5. Bảng volumes (tập truyện)
CREATE TABLE volumes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  novel_id INT NOT NULL,
  volume_number INT NOT NULL,            -- số thứ tự tập
  title VARCHAR(200),                    -- tiêu đề tập (nếu có)
  description TEXT,                      -- mô tả ngắn về nội dung tập
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE,
  UNIQUE (novel_id, volume_number)       -- mỗi novel chỉ có một volume_number
);

-- 1.6. Bảng chapters (chương của tập)
CREATE TABLE chapters (
  id INT AUTO_INCREMENT PRIMARY KEY,
  volume_id INT NOT NULL,
  chapter_number INT NOT NULL,           -- số thứ tự chương trong tập
  title VARCHAR(200) NOT NULL,           -- tiêu đề chương
  content LONGTEXT NOT NULL,             -- nội dung HTML/Markdown đã render
  word_count INT,                        -- tùy chọn: đếm số từ
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (volume_id) REFERENCES volumes(id) ON DELETE CASCADE,
  UNIQUE (volume_id, chapter_number)
);

CREATE TABLE email_verifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  code VARCHAR(10) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

