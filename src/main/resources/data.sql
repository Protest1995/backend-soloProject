-- PostgreSQL: 安全初始化預設管理員帳號（冪等）
-- 若資料庫中已存在相同 username 或 email，則不進行插入

INSERT INTO users (
  id, username, email, password, avatar_url, role, created_at, updated_at
) VALUES (
  'admin-001',
  'admin',
  'admin@example.com',
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
  '/images/profile.jpg',
  'SUPER_USER',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
)
ON CONFLICT DO NOTHING;


