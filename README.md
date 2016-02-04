# Overview

The project integrates [Plantuml](http://plantuml.com/) into [Fitnesse](http://www.fitnesse.org/) wiki.

# Installation

1. Install [Graphviz](http://www.graphviz.org/Download.php) on machine where your Fitnesse is running.
2. Copy jar file from [this project Releases](https://github.com/sbellus/fitnesse-plantuml-plugin/releases) to plugins directory of your Fitnesse.
3. Restart Fitnesse

## Configuration

The configuration is optional. Plugin will work also without any configuration.
Plugin loads its configuration from file ```fitnesse-plantuml-plugin.properties``` located in same directory as jar file. Usually in plugins directory of your Fitnesse.

### Default style
It is optional. When it is set in configuration file, all generated plantuml pictures will use this style. 
```
defaultStyle =                               \
    skinparam monochrome true \n             \
    skinparam backgroundColor transparent \n \
    skinparam shadowing false \n             \
    hide footbox 
```

# Usage

After installation and Fitnesse restart you should be able to use command on wiki
```
!startuml
fitnesse -> plantuml : generate
!enduml
```

The command ```!startuml``` has following syntax ```!startuml ["title"] [align] [width] [height]```
* Title has to be surronded by "" 
* align can be one of
  * c - center
  * r - right
  * l - left
* width is width in pixels 
* height is height in pixels

# Thanks
I would like to thank [Tibor Trnovsky](https://sk.linkedin.com/in/tibor-trnovsky-b9774744) for idea how to align picture in HTML with ```<div>``` tags ([GraphicsSvg.java](https://github.com/sbellus/fitnesse-plantuml-plugin/blob/master/src/main/java/com/github/sbellus/fitnesse/plantuml/graphics/GraphicsSvg.java)).
