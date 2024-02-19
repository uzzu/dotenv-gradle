## Example: Hierarchical dotenv definitions

This works for version ≥ 2.0.0.

You can define environment variables in a hierarchical project. This feature reduces complexity for environment variable names in such project structures.

```
(Root).
├── .env                 # Adds extension to all subprojects.
├── .env.template        # Adds extension to all subprojects.
├── build.gradle.kts
├── settings.gradle.kts
├── foo
│   ├── .env             # Adds extension only to subproject ':foo'.
│   ├── .env.template    # Adds extension only to subproject ':foo'.
│   ├── build.gradle.kts
│   └── src
└── bar
    ├── .env             # Adds extension only to subproject ':bar'.
    ├── .env.template    # Adds extension only to subproject ':bar'.
    ├── build.gradle.kts
    └── src
```

If an environment variable with the same name as the parent project is declared in a child project, the value specified
in the child project takes precedence in the child project.

The hierarchy of this feature means not a directory structure, but a parent-child relationship of Projects. Supporting
entire directory hierarchy is out of scope, as shown below.

```
(Root).
├── .env                 # Adds extension to all subprojects.
├── .env.template        # Adds extension to all subprojects.
├── build.gradle.kts
├── settings.gradle.kts
└── subprojects
    ├── .env             # [!] Does not refer.
    ├── .env.template    # [!] Does not refer.
    ├── foo
    │   ├── .env             # Adds extension only to subproject ':subprojects:foo'.
    │   ├── .env.template    # Adds extension only to subproject ':subprojects:foo'.
    │   ├── build.gradle.kts
    │   └── src
    └── bar
        ├── .env             # Adds extension only to subproject ':subprojects:bar'.
        ├── .env.template    # Adds extension only to subproject ':subprojects:bar'.
        ├── build.gradle.kts
        └── src
```

The reason is that in Gradle, the directory hierarchy only means default name of subproject (so it can be changed).

Run `./gradlew clean` and see console outputs.

Edit `build.gradle.kts`, `sub1/build.gradle.kts`, `sub2/build.gradle.kts` or each `.env` file.

Then run to see if how it works.

## Old Versions

Note that the plugin until version 1.x referred only to an `.env` file in the root project directory, and could not create environement variable extensions which are only used in subproject.
