# Ornithe Carpet Mod

This is an ornithe port of [carpetmod112](https://github.com/gnembon/carpetmod112) but with modern [fabric-carpet](https://github.com/gnembon/fabric-carpet) protocol and extension support.

Code base is adapted from fabric-carpet, features are from carpetmod112.

## Extensions

Now with full carpet extension support just like fabric-carpet!

No more "everyone forks a carpetmod112".

Use this [template](https://github.com/CrazyHPi/ornithe-carpet-extension-template) to create your own carpet extension.

## Carpet Mod Settings

TODO

## Build and Use

Check out [Ornithe](https://ornithemc.net/) for mod loader.

### Using Java > 8

For carpet logger: `rng`

Add jvm flag `--add-opens java.base/java.util=ALL-UNNAMED` to allow reflection on `java.util.Random`.

### Build

* Clone the repo.
* Run `gradle build` task.
* Complied `.jar` file will be in build/libs/.
