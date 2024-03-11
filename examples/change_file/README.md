## Example: Change the `.env` filename

This works with versions ≥ 2.0.0.

This example shows you how to use a different name for the `.env` file. In this example, the `.env.staging` file can be used as the alternative env file. The default filename `.env` is overriden in `gradle.properties`, but commented out.

Run the following command to confirm that the DotEnv file to be loaded has changed.

- `./gradlew clean`
- Edit `gradle.properties`, and `./gradlew clean`

### Migrating from 1.x

In earlier versions of this plugin, the filename could be changed with the `ENV_FILE` environment variable. This is no longer possible as of version 2.0.0.

Please use `dotenv.filename` in gradle.properties instead of `ENV_FILE`.

---

### Old spec

version since: 1.2.0 < 2.0.0

Run the following command to confirm that the DotEnv file to be loaded has changed.

- `./gradlew clean`
- `ENV_FILE=.env.staging ./gradlew clean`
