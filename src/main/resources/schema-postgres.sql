-- PostgreSQL 資料庫初始化腳本
-- 適用於 solo_project 資料庫

-- 創建用戶表
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'SUPER_USER')),
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'NOT_SPECIFIED')),
    birthday TIMESTAMP,
    address VARCHAR(500),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 創建刷新令牌表
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);

-- 數據插入由 DataInitializer 處理 

-- 作品集表
CREATE TABLE IF NOT EXISTS portfolio_items (
    id VARCHAR(64) PRIMARY KEY,
    image_url VARCHAR(1000),
    title VARCHAR(500),
    title_zh VARCHAR(500),
    category_key VARCHAR(100),
    views INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 部落格文章表
CREATE TABLE IF NOT EXISTS blog_posts (
    id VARCHAR(64) PRIMARY KEY,
    image_url VARCHAR(1000),
    is_locked BOOLEAN,
    created_at BIGINT,
    category_key VARCHAR(100),
    likes INT,
    comments_count INT,
    views INT,
    is_featured BOOLEAN,
    title VARCHAR(500),
    title_zh VARCHAR(500),
    excerpt TEXT,
    excerpt_zh TEXT,
    content TEXT,
    content_zh TEXT,
    date TIMESTAMP,
    created_at_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 留言表（若尚未存在）
CREATE TABLE IF NOT EXISTS comments (
    id VARCHAR(36) PRIMARY KEY,
    post_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    username VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500) NOT NULL,
    text TEXT NOT NULL,
    parent_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT comments_post_id_fkey FOREIGN KEY (post_id) REFERENCES blog_posts (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments (created_at);
CREATE INDEX IF NOT EXISTS idx_comments_parent_id ON comments (parent_id);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments (post_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments (user_id);