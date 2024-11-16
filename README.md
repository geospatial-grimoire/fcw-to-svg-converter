# FCW to SVG Converter

**FCW to SVG Converter** allows you to easily transform FCW binary files created in [Campaign Cartographer (CC3+)](https://www.profantasy.com/products/cc3.asp) into the more universal and widely supported SVG format. This is particularly useful for those who wish to view or edit maps from, for example, the [Forgotten Realms Interactive Atlas (1999)](https://forgottenrealms.fandom.com/wiki/Forgotten_Realms_Interactive_Atlas), in modern vector graphic editors.

## Features

- Easy Conversion: Convert FCW files to SVG with minimal effort.
- Cross-Platform: The program is packaged into a single JAR file and runs on any system with Java installed.

## How to Use

1. Ensure you have the latest version of Java installed.
2. [Download the Converter JAR file](https://github.com/geospatial-grimoire/fcw-to-svg-converter/releases/latest) from the Releases section of this repository.
3. Run the Converter:
  - Open a command prompt or terminal.
  - Enter the command: `java -jar fcw-to-svg-converter.jar your_file.fcw`.
4. After executing the command, a file with the `.svg` extension will appear in the same directory.

## Testing

The FCW conversion using the compiled JAR file has been tested on the following setups:

- **Windows 11 Pro 24H2** with Java(TM) SE Runtime Environment (build 22.0.1+8-16)
- **Linux Ubuntu 22.04.5 LTS** with OpenJDK Runtime Environment (build 11.0.24+8-post-Ubuntu-1ubuntu322.04)

When converting my FCW files, the process failed in only one out of 70 cases, for reasons unknown to me.

## Limitations

- This tool does not provide support for PNG-based diagrams.
- CC3 "strokes" are typically fixed in width, regardless of scale factor, whereas SVG requires specifying an actual stroke width.
- CC3 may use fonts that are unavailable or difficult to find for SVG rendering.
- SVG does not inherently support layers, but tools like Inkscape use an extension of SVG "groups" to emulate them. These SVG groups (and, by extension, Inkscape layers) follow a specific order, which can sometimes lead to issues, especially with isometric maps.

## Acknowledgments

This program originates from the [CartographerSVG](https://bitbucket.org/bcholmes/cartographersvg/src/master/) project, published by BC Holmes in 2017, with all copyrights belonging to them. I have simply compiled the source code with the necessary dependencies and packaged it into a convenient JAR file for easier use. A copy of BC Holmes' code, along with the dependent libraries, is included in the `source` folder for preservation purposes.

## Feedback

I'm not a Java developer, so I won't be able to fix any code-related issues. However, if you'd like to provide feedback, please do so in the Issues section of this repository.
