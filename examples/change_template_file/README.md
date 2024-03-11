## Example: Template files, changing `.env.template` filename

This example works with versions ≥ 2.1.0.

This example shows you how to use a template `.env` file, and how to change its name. In this example, the `.env.example` file is used as a template for generating the final environment. The default filanem `.env.template` is overriden in `gradle.properties`.

You can see that `FOO=` is set in the `.env.example` file, and `BAZ` is set in the `.env` file.

Run the following command to confirm that the DotEnv template file to be loaded has changed.

- `./gradlew clean`
- Edit `gradle.properties`, and `./gradlew clean`
