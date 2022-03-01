## Hierarchical dotenv definitions example

version since: 2.0.0

The plugin until version 1.x, refers only to `.env` file in root project directory, and could not create environement
variable extension which only to use in subproject. So organization of environment variable names makes complicated for
a bit. (e.g. Appends prefix to name.)

This feature reduces complexity of environment variable names.

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

Because in Gradle, the directory hierarchy only means default name of subproject. (so it can be changed)

Run `./gradlew clean` and see console outputs.

Edit `build.gradle.kts`, `sub1/build.gradle.kts`, `sub2/build.gradle.kts` or each `.env` file.

Then run to see if how it works.
