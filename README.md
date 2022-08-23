[![Build](https://github.com/promcteam/proskillapi/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/promcteam/promccore/packages/1203744)
[![Build](https://github.com/promcteam/proskillapi/actions/workflows/publish.yml/badge.svg?branch=development)](https://github.com/promcteam/promccore/packages/1203744)

# ProSkillAPI

Our fork is based on the original skillapi and the forked skillapi by Sentropic.

* Includes all premium features from the original premium version of Skillapi found on spigot.

## New dynamic editor

You'll need to use this editor in order for your classes and skills to be compatible with ProSkillAPI.

* Online Editor: https://promcteam.github.io/proskillapi/

## Downloads

You can download ProSkillAPI form our marketplace

* https://promcteam.com/resources/

## PROMCTEAM:

* Discord | https://discord.gg/6UzkTe6RvW

# Development

If you wish to use ProSkillAPI as a dependency in your projects, include the following in your `pom.xml`

```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/promcteam/promccore</url>
</repository>
        ...
<dependency>
    <groupId>mc.promcteam</groupId>
    <artifactId>proskillapi</artifactId>
    <version>VERSION</version>
</dependency>
```

Find version numbers [here](https://github.com/promcteam/promccore/packages/1203744).

Additionally, you'll need to make sure that you have properly
configured [Authentication with GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
.

You may also use JitPack!
