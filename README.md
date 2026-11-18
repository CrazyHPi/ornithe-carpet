# Ornithe Carpet Mod

This is an ornithe port of [carpetmod112](https://github.com/gnembon/carpetmod112) but with modern [fabric-carpet](https://github.com/gnembon/fabric-carpet) protocol and extension support.

Code base is adapted from fabric-carpet, features are from carpetmod112 and others.

## Extensions

Now with full carpet extension support just like fabric-carpet!

No more "everyone forks a carpetmod112".

Use this [template](https://github.com/CrazyHPi/ornithe-carpet-extension-template) to create your own carpet extension.

### Existing Extensions

- [Haruhikage Addon (Threadstone)](https://github.com/Angarn/haruhikage-addon)
- [Ornithe-Carpet-Extra](https://github.com/CrazyHPi/Ornithe-Carpet-Extra)

### Maven

You can get ornithe-carpet as dependency from:
 - [My Maven](https://fastdl.crazyh.fun/maven): `https://fastdl.crazyh.fun/maven`
 - [Jitpack](https://jitpack.io/#CrazyHPi/ornithe-carpet): `https://jitpack.io`

## Carpet Mod Settings

[Feature List](./docs/Current-Available-Settings.md).

## Contribution

Contributions are welcome.

See [Contributing](./docs/Contributing.md)

## Build and Use

Check out [Ornithe](https://ornithemc.net/) for mod loader.

### Using Java > 8

For carpet logger: `rng`

Add jvm flag `--add-opens java.base/java.util=ALL-UNNAMED` to allow reflection on `java.util.Random`.

### Build

* Clone the repo.
* Run `gradle build` task.
* Complied `.jar` file will be in build/libs/.

### Calamus (intermediary) generation

This mod will move to Ornithe Gen2 since commit [731cab21](https://github.com/CrazyHPi/ornithe-carpet/commit/731cab21).

`v0.2.2` is the last version supporting Gen1.
