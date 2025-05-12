# Jfiglol: Animated Lolcat with Figlet

<p align="center">
   <img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif" alt="Jfiglol Animation"/>
</p>

## Requirements

- **JDK 21**: Install using [SDKMAN](https://sdkman.io/install) if you are on Linux.
- **GraalVM Native-Image**: Refer to the [GraalVM Native-Image](#graalvm-native-image) section to build a native application.

## Build

```bash
./gradlew shadowJar
sudo cp build/libs/jfiglol-*-all.jar /bin/jfiglol.jar
echo "alias jfiglol='java -jar /bin/jfiglol.jar' $@" >> ~/.bashrc # or replace with '.your_shellrc_file'
```

## Usage

- Use the file named `jfiglol`. For convenience, copy it to `/usr/bin/`.

```bash
./jfiglol "Your text here"
# Don't forget to use quotes if your text contains spaces.

./jfiglol "Some text" -a
# To animate the text.

./jfiglol "Hello, world!" -a -t /path/to/font.flf
# To specify a font file.
```

- [FLF Fonts Collection](https://github.com/xero/figlet-fonts)

## GraalVM Native-Image

### Requirements

- **Java GraalVM 21**: [Installation Guide](https://www.graalvm.org/docs/getting-started/). You can also use [SDKMAN](https://sdkman.io/install) for Linux.
- **Native-Image**: [Installation Guide](https://www.graalvm.org/reference-manual/native-image/).

### Build a Native Image

- Using Gradle:

```bash
./gradlew nativeBuild
cd build/native/nativeCompile
./jfiglol "Hello, world!" -a
```

---

## Micronaut 3.8.6 Documentation

- [User Guide](https://docs.micronaut.io/3.8.6/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.6/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.6/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

## Useful Links

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Lombok Documentation](https://docs.micronaut.io/latest/guide/index.html#lombok)
- [Lombok Features](https://projectlombok.org/features/all)
