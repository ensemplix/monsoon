Monsoon [![Build Status](https://travis-ci.org/Ensemplix/Monsoon.svg?branch=master)](https://travis-ci.org/Ensemplix/Monsoon)
============

Мы как никто другие знаем все сложности при создании новых команд. В нашем проекте используется дюжина различных команд.
Отличия этой библиотеки от любых других заключаются в генерации объектов на лету без необходимости парсить строку аргументов
от пользователя.

## Подключение

Для подключения библиотеки в своем проекте необходимо использовать Maven или Gradle.

### Maven
```xml
<repositories>
    <repository>
        <id>Ensemplix</id>
        <url>http://maven.ensemplix.ru/artifactory/Ensemplix</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ru.ensemplix.command</groupId>
        <artifactId>Monsoon</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
    maven {
        url 'http://maven.ensemplix.ru/artifactory/Ensemplix/'
    }
}

dependencies {
   compile('ru.ensemplix.command:Monsoon:1.0')
}
```
## Использование
Дальше приведены примеры для работы с библиотекой.
### Создание команды
Количество имен команды не ограничено, но они не должны повторяться. Если команда будет не найдена, то будет брошено исключение ```CommandNotFoundException```. Количество подкоманд в объекте неограниченно.
```java 
// Для работы с командами нужно унаследоваться от интерфейса CommandSender.
public class FooSender implements CommandSender {
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
}
```
```java
// Вот так выглядит создание команды.
public class FooCommand {
    @Command
    public void hello(FooSender sender, String name) {
        sender.sendMessage("Hello " + name);
    }
}
```
```java
// Создаем обработчик команд.
CommandDispatcher dispatcher = new CommandDispatcher();
// Регистрируем команду.
dispatcher.register(new FooCommand(), "test");
// Вызываем команду.
dispatcher.call(new FooSender(), "/test hello koala")
```
```
Hello Koala
```
### Парсер объектов
Библиотека может конвертировать не только простые типы в объекты, но для этого нужно написать небольшой парсер. 
```java
public class FooRegion {
    private final String name;
    
    public FooRegion(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
```
```java
public class FooRegionParser implements TypeParser<FooRegion> {
    @Override
    public FooRegion parse(String value) {
        return new FooRegion(value);
    }
}
```
```java
public class FooRegionCommand {
   @Command
   public void create(FooSender sender, FooRegion region) {
       sender.sendMessage("You created region " + region.getName());
   }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooRegionCommand(), "region");
// Регистрируем парсер.
dispatcher.bind(new FooRegionParser());
// Вызываем команду.
dispatcher.call(new FooSender(), "/region create moon");
```
```
You created region moon
```
### Перечисление
Перечисление множества происходит с помощью коллекции. Поддерживаются любые типы объектов.
```java
public class FooCollection {
    @Command
    public void test(FooSender sender, Collection<String> players) {
        for(String player : players) {
            System.out.println(player);
        }
    }
}
```
```java
// Регистрируем команду.
dispatcher.register(new FooCollection(), "collection");
// Вызываем команду.
dispatcher.call(new FooSender(), "/collection test Ensirius Invincible Koala Elon");
```
```
Ensirius
Invincible
Koala
Elon
```
