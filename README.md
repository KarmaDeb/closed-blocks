# ClosedBlocks

ClosedBlocks is a minecraft plugin which adds multiple blocks with different funcionalities to your
minecraft server. Compatible with multiple plugins which add custom blocks (i.e [ItemsAdder](https://www.spigotmc.org/resources/%E2%9C%A8itemsadder%E2%AD%90emotes-mobs-items-armors-hud-gui-emojis-blocks-wings-hats-liquids.73355/)) and highly
customizable.

## How to install

Installing ClosedBlocks is extremely easy. First of all, make sure you are using a spigot/paper at least for minecraft 1.21 and that you have java 21 installed.
After that, download the resource from where you purcharsed it, and simply drag it into your plugins folder.

## Compiling from source

ClosedBlocks follows a freemium model, in where you can compile the plugin source code yourself and start using the plugin for free, with the only drawback of no
official support. This means that, any bug, or issue that happens while using a self-compiled version of the plugin will be ignored and won't be fixed.

> [!WARNING]
> The plugin is licensed under [AGPL v3](https://www.gnu.org/licenses/agpl-3.0.en.html#license-text). This means that any modification or re-distribution of the
> plugin must be also open-source and have the exact same license.

### Using an IDE (recommended)

Using an IDE is the most recommended option as they usually provide ways of compiling a maven project with less effort that it would require doing it manually. The
recommended IDE is [JetBrains IntellIJ](https://www.jetbrains.com/idea/download/#community-edition) or [Eclipse IDE](https://www.eclipse.org/downloads/). We do not 
recommend using [Visual Studio Code](https://code.visualstudio.com/) as it is not an IDE, regardless of their extense extension environment which provides supports to java maven projects.

If you are using an IDE, all you need to do is to clone the repository `git clone https://github.com/tagfull/closed-blocks.git` and then open the `closed-blocks` directory
using your IDE. The IDE should automatically detect the maven project, and allow you to easily build it without much effort.
- ![Building a maven project in IntellIJ](./documentation/jetbrains.gif)
- ![Building a maven project in Eclipse IDE](./documentation/eclipse.gif)
