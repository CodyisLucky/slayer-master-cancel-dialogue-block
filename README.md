# Slayer Master Cancel Dialogue Block

RuneLite plugin that replaces one chat line: **Can I cancel that task?** shown right after a Slayer master assigns a task. That slot instead reads **Cancel option blocked by plugin**. Choosing the original line spends Slayer points. The plugin does not cancel, skip, or choose any dialogue for you.

Other lines stay where they are, including "Okay, great!", tips, location questions, the rewards shop, and the separate menu for cancelling a task that has become impossible.

Launch the development client from this directory with `./gradlew run`. WSL has no system Java; a Temurin 17 JDK used to compile this project is at `~/.local/jdks/jdk-17.0.21+9`. The plugin bytecode still targets Java 11.

```
export JAVA_HOME="$HOME/.local/jdks/jdk-17.0.21+9"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew run
```
