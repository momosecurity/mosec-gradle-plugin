# MOSEC-GRADLE-PLUGIN

用于检测gradle项目的第三方依赖组件是否存在安全漏洞。

该项目灵感来自 [snyk-gradle-plugin](https://github.com/snyk/snyk-gradle-plugin.git) 。

## 关于我们

Website：https://security.immomo.com

WeChat:

<img src="https://momo-mmsrc.oss-cn-hangzhou.aliyuncs.com/img-1c96a083-7392-3b72-8aec-bad201a6abab.jpeg" width="200" hegiht="200" align="left" />

## 版本支持

Gradle >= 3.0

## 安装

向顶层build.gradle中增加如下配置
```groovy
// file: build.gradle

buildscript {
    repositories {
        maven { url "https://raw.github.com/momosecurity/mosec-gradle-plugin/master/mvn-repo/" }
    }

    dependencies {
        classpath 'com.immomo.momosec:mosec-gradle-plugin:1.1.3'
    }
}

allprojects {
    apply plugin: 'mosec'
}
```

## 使用

首先运行 [MOSEC-X-PLUGIN Backend](https://github.com/momosecurity/mosec-x-plugin-backend.git)

#### 命令行使用

```shell script
# 对于所有projects，Maven项目
> MOSEC_ENDPOINT=http://127.0.0.1:9000/api/plugin \
  ./gradlew --no-parallel \
  mosec -PprojectType=Maven -PonlyProvenance=true

# 对于单个project (如 demo)，Maven项目
> MOSEC_ENDPOINT=http://127.0.0.1:9000/api/plugin \ 
  ./gradlew --no-parallel \
  :demo:mosec -PprojectType=Maven -PonlyProvenance=true

# 对于 Android 项目，使用 confAttr 参数
> MOSEC_ENDPOINT=http://127.0.0.1:9000/api/plugin \ 
  ./gradlew --no-parallel \
  mosec -PprojectType=Android -PconfAttr=buildtype:release -PonlyProvenance=true
```

## 开发

#### Intellij 远程调试 Gradle 插件

1.将mosec-gradle-plugin安装至本地仓库

2.git clone mosec-gradle-plugin

3.Intellij 中新建 Remote Configuration 并填入如下信息

![remote-configuration](https://github.com/momosecurity/mosec-gradle-plugin/blob/master/static/remote-configuration.jpg)

4.在另一个gradle工程中执行如下命令

```shell script
> ./gradlew --no-parallel --no-daemon mosec -Dorg.gradle.debug=true
```

5.回到Intellij中，下断点，开始Debug