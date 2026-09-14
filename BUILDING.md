# 📱 Android 广告拦截器 - 构建指南

本文档将指导您如何从源代码构建安装包(APK)，以便在Android设备上安装使用。

## 📋 前置条件

在开始构建之前，请确保您已安装以下软件：

### 必需软件
1. **Java Development Kit (JDK) 11 或更高版本**
   - 下载地址：https://www.oracle.com/java/technologies/downloads/
   - 安装后请配置 `JAVA_HOME` 环境变量

2. **Android Studio** (推荐) 或 **Android SDK Command-line Tools**
   - Android Studio下载：https://developer.android.com/studio
   - 安装时请确保勾选：
     - Android SDK
     - Android SDK Platform
     - Android Virtual Device
     - Performance (Intel ® HAXM) （可选，用于加速模拟器）
   - 或仅安装 Command-line Tools：https://developer.android.com/studio#command-tools

3. **Git** (用于克隆仓库)
   - 下载地址：https://git-scm.com/downloads

### 环境变量配置
请确保以下环境变量正确设置：
- `JAVA_HOME`：指向JDK安装目录
- `ANDROID_HOME` 或 `ANDROID_SDK_ROOT`：指向Android SDK安装目录
- 将以下目录添加到系统`PATH`：
  - `%JAVA_HOME%\bin`
  - `%ANDROID_HOME%\platform-tools`
  - `%ANDROID_HOME%\cmdline-tools\latest\bin` (如果使用command-line tools)

## 📥 步骤1：获取源代码

### 方法A：通过Git克隆（推荐）
```bash
git clone https://github.com/huvfghvhuv-ai/adblocker-android.git
cd adblocker-android
```

### 方法B：下载ZIP文件
1. 访问：https://github.com/huvfghvhuv-ai/adblocker-android/releases
2. 下载最新版本的源代码 ZIP 文件
3. 解压到您想要的目录
4. 在命令行中进入该目录

## 🔨 步骤2：构建APK

### 选项1：使用Android Studio（推荐，图形界面）

1. 打开Android Studio
2. 选择 "Open an existing Project"
3. 导航到您克隆的 `adblocker-android` 目录
4. 选择 `build.gradle` 文件打开项目
5. 等待Android Studio完成项目同步和索引（底部进度条）
6. 在顶部菜单栏选择：`Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
7. 等待构建完成（通常需要1-3分钟，首次构建可能更长）
8. 构建成功后，会弹出通知，点击 "locate" 或手动导航到：
   `app/build/outputs/apk/debug/app-debug.apk`

### 选项2：使用命令行

```bash
# 确保您在项目根目录（包含gradlew.bat的目录）
cd adblocker-android

# 生成调试版APK（用于测试和个人使用）
./gradlew assembleDebug

# 生成发布版APK（需要签名，适合分布）
./gradlew assembleRelease

# 构建完成后，APK文件位置：
# 调试版: app/build/outputs/apk/debug/app-debug.apk
# 发布版: app/build/outputs/apk/release/app-release-unsigned.apk
```

> ⚠️ 注意：发布版APK需要签名才能安装到设备上。调试版可以直接安装到开启了USB调试的开发者设备或模拟器上。

## 📱 步骤3：安装和测试

### 方法A：通过USB调试安装（推荐）
1. 在Android设备上：
   - 打开 "设置" → "关于手机" → 连续点击 "版本号" 7次以开启开发者选项
   - 返回 "设置" → "系统" → "开发者选项"
   - 开启 "USB调试"
   - （可选）开启 "通过USB安装"
2. 使用USB数据线将设备连接到电脑
3. 在命令行中执行：
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
4. 如果看到 `Success`，则安装成功

### 方法B：通过模拟器安装
1. 在Android Studio中打开 AVD管理器（Tools → AVD Manager）
2. 创建或启动一个模拟器设备
3. 构建APK后，将APK文件拖放到模拟器窗口中即可安装
4. 或使用命令行：
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### 方法C：通过邮件或云盘分发
1. 将生成的APK文件通过邮件、微信、QQ或云盘发送到您的Android设备
2. 在设备上点击APK文件进行安装
3. 系统可能会提示“来自未知来源的应用被阻止”，请在设置中临时允许安装：
   - 设置 → 安装未知应用 → [您的文件管理器或浏览器] → 允许来自此来源的安装

## 🔐 步骤4：生成签名APK（用于正式分发）

如果您想要分享APK给其他用户（不通过开发者选项或USB调试），需要生成签名版本：

### 方法A：使用Android Studio
1. `Build` → `Generate Signed Bundle / APK`
2. 选择 `APK` → `Next`
3. 创建新的密钥库或选择现有密钥库
4. 填写密钥库信息：
   - Key store path：选择保存位置
   - Password：密钥库密码
   - Key alias：密钥别名
   - Key password：密钥密码（可以与密钥库密码相同）
5. 选择构建类型：`release`
6. 点击 `Finish` 等待构建完成
7. 签名APK位置：`app/build/outputs/apk/release/app-release.apk`

### 方法B：使用命令行（高级用户）
```bash
# 1. 生成密钥库（仅需执行一次）
keytool -genkeypair -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias

# 2. 构建并签名
./gradlew assembleRelease
# 然后使用 jarsigner 或 apksigner 签名（详见Android官方文档）
```

## 🛠️ 常见问题排错

### Q: 找不到 gradlew 或 gradlew.bat 文件？
A: 请确保您在项目根目录（包含 `build.gradle` 和 `settings.gradle` 的目录）中执行命令。

### Q: 构建时出现 "failed to find target with hash string 'android-34'"？
A: 您需要安装对应的Android平台：
   - 在Android Studio中：SDK Platforms → 显示包详情 → 勾选 Android 14.0 (API 34) → Apply
   - 或使用命令行：`sdkmanager "platforms;android-34"`

### Q: 构建超时或内存不足？
A: 尝试增加Gradle堆内存：
   在 `gradle.properties` 中添加或修改：
   ```
   org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
   ```

### Q: 安装时提示 "Failed to install ...: Failure [INSTALL_FAILED_INVALID_APK: Split lib_slice_9_apk was not defined]"？
A: 请确保您安装的是完整的APK而非App Bundle。使用 `Build APK(s)` 而非 `Build Bundle(s)`。

### Q: 设备上安装后打开应用闪退？
A: 请检查：
   1. 是否已开启无障碍服务（应用会引导您到设置中开启）
   2. 是否有其他冲突的无障碍服务
   3. 查看Logcat输出获取详细错误信息

## 📝 使用说明（构建后）

首次打开应用时：
1. 应用会提示您启用无障碍服务
2. 按照提示进入系统设置 → 辅助功能 → 广告拦截器 → 打开使用开关
3. 返回应用，您应该会看到状态变为“无障碍服务已启用 - 广告拦截器正在运行”
4. 现在应用将在后台监控窗口变化并自动关闭检测到的广告弹窗

### 自定义规则
- 点击主界面的“管理规则”按钮
- 添加自定义规则：包名正则、关键词、按钮文字
- 例如：为了拦截某个应用的广告弹窗，可以添加规则：
  - 包名: `.*com\\.example\\.adapp.*`
  - 关键词: 广告,赞助,推广
  - 按钮: 关闭,×,Skip

### 查看日志
- 点击主界面的“查看日志”按钮
- 查看被拦截的广告记录，包含时间、包名和窗口文本

## 🙋‍♂️ 问题反馈

如果您在构建或使用过程中遇到任何问题，请在GitHub仓库的Issues部分提交：
https://github.com/huvfghvhuv-ai/adblocker-android/issues

祝您使用愉快！如果这个应用帮助您减少了广告骚扰，欢迎星标⭐仓库以支持后续开发。