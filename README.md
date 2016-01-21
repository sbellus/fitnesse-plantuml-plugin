# Overview

The project integrates [Plantuml](http://plantuml.com/) into [Fitnesse](http://www.fitnesse.org/) wiki.

# Installation

1. Install [Graphviz](http://www.graphviz.org/Download.php) on machine where your Fitnesse is running.
2. Copy jar file from [this project Releases](https://github.com/sbellus/fitnesse-plantuml-plugin/releases) to plugins directory of your Fitnesse.
3. Add following lines to plugins.properties of your Fitnesse
```
{{{
Plugins = com.github.sbellus.fitnesse.plantuml.PlantumlPlugin
#
# Configure plantuml default style.
# Each line MUST BE ended with new line '\n' because it is forwarded to plantuml as it is defined here.
plantuml.defaultStyle =                      \
    skinparam monochrome true \n             \
    skinparam backgroundColor transparent \n \
    skinparam shadowing false \n             \
    hide footbox 
}}} 
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

