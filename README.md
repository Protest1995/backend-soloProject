# Portfolio API Backend

Spring Boot 後端 API 服務，提供用戶認證和作品集管理功能。

## 技術棧

- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全認證
- **Spring Data JPA** - 數據持久化
- **MySQL 8.0** - 資料庫
- **JWT** - 令牌認證
- **Maven** - 依賴管理

## 快速開始

### 前置需求

- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 安裝步驟

1. **克隆專案**
   ```bash
   git clone <repository-url>
   cd backend
   ```

2. **配置資料庫**
   ```bash
   # 登入 MySQL
   mysql -u root -p
   
   # 執行初始化腳本
   source src/main/resources/schema.sql
   ```

3. **配置環境變數**
   創建 `.env` 文件：
   ```bash
   DB_USERNAME=root
   DB_PASSWORD=your_password
   JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-secure
   CORS_ALLOWED_ORIGINS=http://localhost:3000
   ```

4. **啟動應用**
   ```bash
   mvn spring-boot:run
   ```

   或使用 IDE 直接運行 `PortfolioApplication.java`

## API 端點

### 認證相關

- `POST /api/auth/login` - 用戶登入
- `POST /api/auth/register` - 用戶註冊
- `POST /api/auth/refresh` - 刷新令牌
- `POST /api/auth/logout` - 用戶登出

### 請求範例

**登入**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**註冊**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "user@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

## 預設用戶

- **管理員帳號**: `admin`
- **管理員密碼**: `password123`
- **角色**: `SUPER_USER`

## 開發

### 專案結構

```
src/main/java/com/solo/portfolio/
├── PortfolioApplication.java          # 主應用類
├── config/                           # 配置類
│   └── SecurityConfig.java           # 安全配置
├── controller/                       # 控制器
│   └── AuthController.java           # 認證控制器
├── service/                          # 服務層
│   ├── AuthService.java              # 認證服務
│   └── UserDetailsServiceImpl.java   # 用戶詳情服務
├── repository/                       # 數據訪問層
│   ├── UserRepository.java           # 用戶 Repository
│   └── RefreshTokenRepository.java   # 刷新令牌 Repository
├── model/                           # 數據模型
│   ├── entity/                      # 實體類
│   │   ├── User.java                # 用戶實體
│   │   ├── RefreshToken.java        # 刷新令牌實體
│   │   ├── UserRole.java            # 用戶角色枚舉
│   │   └── Gender.java              # 性別枚舉
│   └── dto/                         # 數據傳輸對象
│       ├── AuthRequest.java         # 認證請求
│       ├── AuthResponse.java        # 認證響應
│       ├── RegisterRequest.java     # 註冊請求
│       └── UserDto.java             # 用戶 DTO
└── security/                        # 安全相關
    └── JwtTokenProvider.java        # JWT 令牌提供者
```

### 建置

```bash
# 編譯
mvn compile

# 測試
mvn test

# 打包
mvn package

# 運行 JAR
java -jar target/portfolio-api-0.0.1-SNAPSHOT.jar
```

## 部署

### Docker 部署

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/portfolio-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# 建置 Docker 映像
docker build -t portfolio-api .

# 運行容器
docker run -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=your-secret \
  portfolio-api
```

## 配置說明

### 應用配置 (application.yml)

- **資料庫連接**: MySQL 配置
- **JWT 設定**: 令牌密鑰和過期時間
- **CORS 設定**: 跨域請求配置
- **伺服器端口**: 8080

### 安全配置

- **無狀態認證**: 使用 JWT 令牌
- **密碼加密**: BCrypt 加密
- **CORS 支援**: 允許跨域請求
- **端點保護**: 除認證端點外都需要認證

## 故障排除

### 常見問題

1. **資料庫連接失敗**
   - 檢查 MySQL 服務是否運行
   - 確認資料庫憑證正確
   - 檢查防火牆設定

2. **JWT 令牌無效**
   - 確認 JWT_SECRET 環境變數設定
   - 檢查令牌是否過期
   - 驗證令牌格式

3. **CORS 錯誤**
   - 檢查 CORS_ALLOWED_ORIGINS 設定
   - 確認前端域名正確

## 貢獻

1. Fork 專案
2. 創建功能分支
3. 提交變更
4. 發起 Pull Request

## 授權

MIT License 