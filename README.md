# Dotenv Gradle with Resources

### Forked from [uzzu/dotenv-gradle](https://github.com/uzzu/dotenv-gradle)

A Gradle plugin fork that extends the original Dotenv Gradle plugin by adding support for processing resource files during the `processResources` phase.


## Example Usage

### 1. Example `.env` file

```dotenv
FOO=hello
BAR=world
BAZ=123
```

### 2. Resource file **before** processing (`src/main/resources/config.yml`)

```yaml
message: "${FOO}, ${BAR}! Your code is ${BAZ}"
```

### 3. Resource file **after** processing (`build/resources/main/config.yml`)

```yaml
message: "hello, world! Your code is 123"
```
---
## Config

You can adjust path to your env file and which files are processed by setting a pattern in your `gradle.properties`: 

```properties
dotenv.filename=.env
dotenv.resources.pattern=**/*
```


---

For more information, configuration options, and advanced usage, please refer to the original project:
 [uzzu/dotenv-gradle](https://github.com/uzzu/dotenv-gradle)
