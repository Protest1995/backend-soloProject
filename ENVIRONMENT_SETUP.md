# 环境变量设置说明

## 本地开发环境

### 方法1：系统环境变量
在系统环境变量中设置以下值：

```bash
# Windows PowerShell
$env:GOOGLE_CLIENT_ID="your-google-client-id"
$env:GOOGLE_CLIENT_SECRET="your-google-client-secret"
$env:JWT_SECRET="your-jwt-secret-key"

# Windows CMD
set GOOGLE_CLIENT_ID=your-google-client-id
set GOOGLE_CLIENT_SECRET=your-google-client-secret
set JWT_SECRET=your-jwt-secret-key
```

### 方法2：IDE环境变量
在IDE（如IntelliJ IDEA、VS Code）中设置环境变量。

### 方法3：使用默认值
如果不设置环境变量，应用将使用配置文件中的默认值（仅限本地开发）。

## 生产环境

生产环境必须设置以下环境变量：

```bash
GOOGLE_CLIENT_ID=你的Google客户端ID
GOOGLE_CLIENT_SECRET=你的Google客户端密钥
JWT_SECRET=你的JWT密钥
DATABASE_URL=你的数据库连接URL
DATABASE_USERNAME=你的数据库用户名
DATABASE_PASSWORD=你的数据库密码
```

## 注意事项

- 本地开发可以使用默认值
- 生产环境必须使用环境变量
- 不要将包含真实密钥的配置文件提交到git
- 定期轮换生产环境的密钥
