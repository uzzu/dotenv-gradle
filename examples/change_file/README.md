## Change file example

version since: 2.0.0

Run the following command to confirm that the DotEnv file to be loaded has changed.

- `./gradlew clean`
- Edit `gradle.properties`, and `./gradlew clean`

### Migrate from 1.x

Change file feature by using environment variable `ENV_FILE` is removed in 2.0.0.

Please use `dotenv.filename` in gradle.properties instead of `ENV_FILE`.

----

### Old spec

version since: 1.2.0 < 2.0.0

Run the following command to confirm that the DotEnv file to be loaded has changed.

- `./gradlew clean`
- `ENV_FILE=.env.staging ./gradlew clean`
